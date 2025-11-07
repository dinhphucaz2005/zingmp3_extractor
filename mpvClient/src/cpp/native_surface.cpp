#include <mpv/client.h>
#include <mpv/opengl_cb.h>

void playWithMPV(Window x11Window) {
    mpv_handle *mpv = mpv_create();
    if (!mpv) return;

    mpv_initialize(mpv);

    char widStr[64];
    snprintf(widStr, sizeof(widStr), "%lu", (unsigned long)x11Window);
    mpv_set_option_string(mpv, "wid", widStr);

    // Load file/video
    const char *cmd[] = {"loadfile", "https://www.youtube.com/watch?v=sFXGrTng0gQ", NULL};
    mpv_command(mpv, cmd);
}
