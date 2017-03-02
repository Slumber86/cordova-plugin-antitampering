module.exports = function(ctx) {
    var fs = ctx.requireCordovaModule('fs'),
        path = ctx.requireCordovaModule('path'),
        xml = ctx.requireCordovaModule('cordova-common').xmlHelpers,
        exec = require('child_process').exec;
        fs.readFile(ctx.opts.options.buildConfig, 'utf8', function(err, data) {
          if(err) {
            deferral.reject('[Anti tampering]Operation failed');
            return console.log(err);
          }
          var bldcfg = JSON.parse(data);
          var keystoreurl = "";
          if (ctx.opts.options.release === true){
            if("release"in bldcfg.android){
              deferral.reject('[Anti tampering] Operation failed - no release signing config found');
              return console.log(err);
            }
            keystoredef = bldcfg.android.release
          }
          // debug keystore is default user keystore or in buildConfig
          if("debug"in bldcfg.android){
            keystoredef = bldcfg.android.debug
          } else { //default config
            var path = '~/.android/debug.keystore';
            if (process.platform === 'win32'){
              path = '%USERPROFILE%\\.android\\debug.keystore';
            }
            keystoredef = {
                keystore: path,
                storePassword: "",
                alias: "androiddebugkey",
                password : "",
                keystoreType: ""
            }
          }
          //reading fingerprint from keystore

          exec('keytool ' + keystoredef.keystore + '', (error, stdout, stderr) => {
            if (error) {
              console.error(`exec error: ${error}`);
                return;
              }
              console.log(`stdout: ${stdout}`);
              console.log(`stderr: ${stderr}`);
            });
        }
};
