import CryptoJS from "crypto-js";

//const ctime = Math.floor(Date.now() / 1000);
const ctime = "1761978643"
const id = "ZWADD0BF";
const version = "1.17.2";

const e = "ctime=" + ctime + "id=" + id + "version=" + version;

const sha256 = CryptoJS.algo.SHA256.create().finalize(e);

let t = "/api/v2/song/get/streaming" + sha256.toString(CryptoJS.enc.Hex);


const n = "acOrvUS15XRW2o9JksiK1KgQ6Vbds8ZW";

const result = CryptoJS.HmacSHA512(t, n).toString(CryptoJS.enc.Hex);

console.log(result);

const output = `https://zingmp3.vn/api/v2/song/get/streaming?id=${id}&ctime=${ctime}&version=${version}&sig=${result}&apiKey=X5BM3w8N7MKozC0B85o4KMlzLZKhV00y`;

console.log(output);