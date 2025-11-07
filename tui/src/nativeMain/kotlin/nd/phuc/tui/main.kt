package nd.phuc.tui

import kotlinx.cinterop.*
import platform.linux.ioctl
import platform.posix.*

fun listMp3Files(path: String): List<String> {
    val result = mutableListOf<String>()
    val dir = opendir(path) ?: return result
    try {
        while (true) {
            val entry = readdir(dir) ?: break
            val name = entry.pointed.d_name.toKString()
            if (name != "." && name != ".." && name.endsWith(".mp3", ignoreCase = true)) {
                result += name
            }
        }
    } finally {
        closedir(dir)
    }
    return result.sorted()
}

fun String.visibleWidth(): Int {
    val noAnsi = this.replace(Regex("\u001B\\[[;\\d]*m"), "")
    return noAnsi.sumOf { wcwidth(it).coerceAtLeast(0) }
}

fun String.padEndVisible(width: Int, padChar: Char = ' '): String {
    var result = this
    while (result.visibleWidth() < width) {
        result += padChar
    }
    return result
}

fun String.takeVisible(maxWidth: Int): String {
    val regex = Regex("(\u001B\\[[;\\d]*m|.)")
    var width = 0
    val sb = StringBuilder()
    for (match in regex.findAll(this)) {
        val token = match.value
        if (token.startsWith("\u001B")) {
            sb.append(token)
        } else {
            val w = wcwidth(token[0]).coerceAtLeast(0)
            if (width + w > maxWidth) break
            sb.append(token)
            width += w
        }
    }
    return sb.toString()
}

sealed interface Element {
    fun render(): List<String>
}

fun column(
    scrollOffset: Int = 0,
    maxVisible: Int = getMaxTerminalHeight(),
    block: Column.() -> Unit,
): Column {
    val c = Column(
        scrollOffset = scrollOffset,
        maxVisible = maxVisible,
    ); c.block(); return c
}


class State<T>(initial: T) {
    var value: T = initial
        set(v) {
            if (field == v) return
            field = v
            listeners.forEach { it() }
        }
    private val listeners = mutableListOf<() -> Unit>()
    fun onChange(listener: () -> Unit) {
        listeners += listener
    }
}

fun renderToString(root: Element): String = root.render().joinToString("\n")

object AnsiColors {
    const val RESET = "\u001B[0m"
    const val RED = "\u001B[31m"
    const val GREEN = "\u001B[32m"
    const val YELLOW = "\u001B[33m"
    const val BLUE = "\u001B[34m"
    const val MAGENTA = "\u001B[35m"
    const val CYAN = "\u001B[36m"
    const val WHITE = "\u001B[37m"
    const val GRAY = "\u001B[90m"

    const val BG_RED = "\u001B[41m"
    const val BG_GREEN = "\u001B[42m"
    const val BG_YELLOW = "\u001B[43m"
    const val BG_BLUE = "\u001B[44m"
    const val BG_MAGENTA = "\u001B[45m"
    const val BG_CYAN = "\u001B[46m"
    const val BG_WHITE = "\u001B[47m"
    const val BG_GRAY = "\u001B[100m"
}

data class ModifierScope(
    var foregroundColor: String? = null,
    var backgroundColor: String? = null,
    var bold: Boolean = false,
    var paddingLeft: Int = 0,
    var paddingRight: Int = 0,
) {
    fun apply(text: String): String {
        var result = " ".repeat(paddingLeft) + text + " ".repeat(paddingRight)
        val styles = StringBuilder()
        foregroundColor?.let { styles.append(it) }
        backgroundColor?.let { styles.append(it) }
        if (bold) styles.append("\u001B[1m")

        result = if (styles.isNotEmpty()) {
            styles.toString() + result + AnsiColors.RESET
        } else {
            result
        }
        return result
    }
}

enum class Key {
    UP, DOWN, LEFT, RIGHT, ENTER, CTRL_C, CTRL_V,
}

class Screen {
    private val states = mutableListOf<State<*>>()
    private var renderFn: (() -> Unit)? = null
    private val keyHandlers = mutableMapOf<Key, () -> Unit>()
    private val charHandlers = mutableMapOf<Char, () -> Unit>()
    private var running = true

    fun <T> state(initial: T): State<T> {
        val s = State(initial)
        states += s
        s.onChange { renderFn?.invoke() }
        return s
    }

    fun render(block: () -> String) {
        renderFn = {
            print("\u001B[?25l") // ẩn con trỏ
            clearTerminal()
            fflush(stdout)
            print(block())
            fflush(stdout)
        }
        renderFn?.invoke()
    }

    fun exit() {
        running = false
        print("\u001B[?25h") // hiện lại con trỏ
        clearTerminal()
        println("Goodbye!")
    }

    fun onKey(vararg keys: Key, action: () -> Unit) {
        keys.forEach { keyHandlers[it] = action }
    }

    fun onChar(vararg chars: Char, action: () -> Unit) {
        chars.forEach { charHandlers[it] = action }
    }

    fun run(block: Screen.() -> Unit) = memScoped {
        this@Screen.block()
        val oldt = alloc<termios>()
        val newt = alloc<termios>()

        tcgetattr(STDIN_FILENO, oldt.ptr)
        memcpy(newt.ptr, oldt.ptr, sizeOf<termios>().toULong())
        newt.c_lflag = newt.c_lflag and (ICANON.inv().toUInt()) and (ECHO.inv().toUInt())
        tcsetattr(STDIN_FILENO, TCSANOW, newt.ptr)

        try {
            while (running) {

                // Simple non-blocking check
                val c = getchar()

                if (c == EOF) {
                    usleep(10_000u) // 10ms delay when no input
                    continue
                }

                when (c) {
                    0x1B -> {
                        // Escape sequence - read next 2 chars quickly
                        val n1 = getchar()
                        if (n1 == EOF) continue
                        val n2 = getchar()
                        if (n2 == EOF) continue

                        when (n1.toChar()) {
                            '[' -> when (n2.toChar()) {
                                'A' -> keyHandlers[Key.UP]?.invoke()
                                'B' -> keyHandlers[Key.DOWN]?.invoke()
                                'C' -> keyHandlers[Key.RIGHT]?.invoke()
                                'D' -> keyHandlers[Key.LEFT]?.invoke()
                            }
                        }
                    }

                    13 -> keyHandlers[Key.ENTER]?.invoke() // Enter
                    3 -> {
                        keyHandlers[Key.CTRL_C]?.invoke() // Ctrl+C
                        break
                    }

                    22 -> keyHandlers[Key.CTRL_V]?.invoke() // Ctrl+V
                    else -> {
                        val char = c.toChar()
                        if (charHandlers.containsKey(char)) {
                            charHandlers[char]?.invoke()
                        }
                    }
                }
            }
        } finally {
            tcsetattr(STDIN_FILENO, TCSANOW, oldt.ptr)
        }
    }
}

class Text(
    val content: () -> String,
    private val modifier: ModifierScope = ModifierScope(),
) : Element {
    override fun render(): List<String> = listOf(modifier.apply(content()))
}

fun clearTerminal() {
    print("\u001b[H\u001b[2J")
    fflush(stdout)
}

fun showCoverMusicFile(filePath: String) {
    // Tạo hash từ filePath để dùng làm tên cache file
    val hashName = filePath.hashCode().toString()
    val cacheDir = "/home/phuc/.cache/MusicNDP/covers"
    val coverFilePath = "$cacheDir/${hashName}.png"

    // Tạo thư mục cache nếu chưa tồn tại
    mkdir(cacheDir, 0x1C0u)

    // Kiểm tra file cache tồn tại
    val cacheFile = fopen(coverFilePath, "rb")
    if (cacheFile == null) {
        // Extract cover art với fallback methods
        extractCoverArt(filePath, coverFilePath)
    } else {
        fclose(cacheFile)
    }

    // Kiểm tra lại xem file cover đã được tạo chưa
    val finalCheck = fopen(coverFilePath, "rb")
    if (finalCheck == null) {
        showPlaceholderCover()
        return
    }
    fclose(finalCheck)

    // Clear previous image
    print("\u001B_Ga=d\u001B\\")
    fflush(stdout)

    // Show image với kitty icat - optimized placement
    val kittyCmd = "kitty icat --align=right --place=40x40@150x5 '$coverFilePath' 2>/dev/null"
    system(kittyCmd)
}

private var currentMusicProcess: CPointer<FILE>? = null

fun playMusicFile(filePath: String) {
    stopMusic()
    val cmd = "mpv --no-audio-display \"$filePath\""
    currentMusicProcess = popen(cmd, "r")
    showCoverMusicFile(filePath)
}

fun stopMusic() {
    currentMusicProcess?.let {
        pclose(it)
        currentMusicProcess = null
    }

    system("pkill -f 'mpv --no-video' 2>/dev/null")
}


private fun extractCoverArt(filePath: String, coverFilePath: String) {
    // Thử nhiều method để extract cover art
    val methods = listOf(
        "ffmpeg -i \"$filePath\" -an -vcodec copy \"$coverFilePath\" -y >/dev/null 2>&1",
        "ffmpeg -i \"$filePath\" -map 0:v -c copy \"$coverFilePath\" -y >/dev/null 2>&1",
        "ffmpeg -i \"$filePath\" -vf scale=200:200 \"$coverFilePath\" -y >/dev/null 2>&1"
    )

    for (cmd in methods) {
        if (system(cmd) == 0) {
            break
        }
    }
}

private fun showPlaceholderCover() {
    // Clear previous image
    print("\u001B_Ga=d\u001B\\")
    fflush(stdout)

    // Hiển thị placeholder text thay vì ảnh
    val placeholder = """
        ${AnsiColors.GRAY}
        ┌────────────┐
        │   NO       │
        │   COVER    │
        │   ART      │
        └────────────┘
        ${AnsiColors.RESET}
    """.trimIndent()

    // Position placeholder với cursor movement
    print("\u001B[5;70H") // Move cursor to position
    print(placeholder)
    fflush(stdout)
}

fun getMaxTerminalHeight(): Int {
    memScoped {
        val w = alloc<winsize>()
        if (ioctl(STDOUT_FILENO, TIOCGWINSZ.toULong(), w.ptr) == 0) {
            return w.ws_row.toInt() - 2 // Reserve space for header
        }
    }
    return 20 // fallback
}

fun getMaxTerminalWidth(): Int {
    memScoped {
        val w = alloc<winsize>()
        if (ioctl(STDOUT_FILENO, TIOCGWINSZ.toULong(), w.ptr) == 0) {
            return w.ws_col.toInt()
        }
    }
    return 80 // fallback
}

class Column(
    private var scrollOffset: Int,
    private var maxVisible: Int,
) : Element {
    val children = mutableListOf<Element>()

    fun text(content: () -> String, block: ModifierScope.() -> Unit) {
        children += Text(content, ModifierScope().apply(block))
    }

    override fun render(): List<String> {
        val all = children.flatMap { it.render() }
        return all.drop(scrollOffset).take(maxVisible)
    }
}

fun wcwidth(c: Char): Int {
    val code = c.code
    return when (code) {
        in 0x1100..0x115F -> 2
        in 0x2329..0x232A -> 2
        in 0x2E80..0xA4CF -> 2
        in 0xAC00..0xD7A3 -> 2
        in 0xF900..0xFAFF -> 2
        in 0xFE10..0xFE19 -> 2
        in 0xFE30..0xFE6F -> 2
        in 0xFF00..0xFF60 -> 2
        in 0xFFE0..0xFFE6 -> 2
        else -> 1
    }
}

fun main() {
    val musicPath = "/home/phuc/Music/Nightcore"
    val mp3Files = listMp3Files(musicPath)

    if (mp3Files.isEmpty()) {
        println("No MP3 files found in $musicPath")
        return
    }

    val maxLength = getMaxTerminalWidth() - 45 // Reserve space for cover art
    val displayFiles = mp3Files.map {
        val displayName = if (it.visibleWidth() > maxLength) it.takeVisible(maxLength - 3) + "..."
        else it.padEndVisible(maxLength)
        displayName
    }

    Screen().apply {
        val selectedIndex = state(0)
        val playingIndex = state<Int?>(null)

        fun handleUp() {
            selectedIndex.value = (selectedIndex.value - 1).coerceAtLeast(0)
            showCoverMusicFile("$musicPath/${mp3Files[selectedIndex.value]}")
        }

        fun handleDown() {
            selectedIndex.value = (selectedIndex.value + 1).coerceAtMost(mp3Files.size - 1)
            showCoverMusicFile("$musicPath/${mp3Files[selectedIndex.value]}")
        }

        fun handlePlay() {
            playingIndex.value = selectedIndex.value
            playMusicFile("$musicPath/${mp3Files[selectedIndex.value]}")
        }

        onKey(Key.UP) { handleUp() }
        onChar('k', 'K') { handleUp() }
        onKey(Key.DOWN) { handleDown() }
        onChar('j', 'J') { handleDown() }
        onKey(Key.ENTER) { handlePlay() }
        onChar(' ', 'p', 'P') { handlePlay() }
        onKey(Key.CTRL_C) { exit() }
        onChar('q', 'Q') { exit() }

        // Show cover for first file initially
        showCoverMusicFile("$musicPath/${mp3Files[selectedIndex.value]}")
        val header =
            "${AnsiColors.CYAN}🎵 Music Player (${mp3Files.size} tracks) ${AnsiColors.RESET}"
        val footer = "${AnsiColors.GRAY}↑↓: Navigate • Space: Play • Q: Quit${AnsiColors.RESET}"

        run {
            render {
                header + "\n" + footer + "\n" + "\n" + renderToString(
                    column(
                        scrollOffset = (selectedIndex.value - 5).coerceIn(
                            minimumValue = 0, maximumValue = maxOf(
                                0, mp3Files.size - getMaxTerminalHeight()
                            )
                        ), maxVisible = getMaxTerminalHeight()
                    ) {
                        displayFiles.forEachIndexed { index, value ->
                            text(content = {
                                val prefix = when {
                                    playingIndex.value == index -> "${AnsiColors.GREEN}▶ "
                                    selectedIndex.value == index -> "${AnsiColors.YELLOW}❯ "
                                    else -> "  "
                                }
                                prefix + value
                            }, block = {
                                foregroundColor = when {
                                    playingIndex.value == index -> AnsiColors.GREEN
                                    selectedIndex.value == index -> AnsiColors.WHITE
                                    else -> AnsiColors.GRAY
                                }
                                backgroundColor = when {
                                    selectedIndex.value == index -> AnsiColors.BG_BLUE
                                    else -> null
                                }
                                bold = selectedIndex.value == index || playingIndex.value == index
                            })
                        }
                    })
            }
        }
    }
}