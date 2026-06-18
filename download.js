const https = require('https');
const fs = require('fs');

const url = 'https://drive.googleusercontent.com/download?id=128cCsZnRMpH31CI1-HvTsaVyLZGIK4e0&export=download&confirm=t';

https.get(url, (res) => {
    if (res.statusCode >= 300 && res.statusCode < 400 && res.headers.location) {
        https.get(res.headers.location, (res2) => {
            res2.pipe(fs.createWriteStream('app/src/main/res/drawable/ic_app_icon_downloaded.png'));
        });
    } else {
        res.pipe(fs.createWriteStream('app/src/main/res/drawable/ic_app_icon_downloaded.png'));
    }
});
