// Place third party dependencies in the lib folder
//
// Configure loading modules from the lib directory,
// except 'app' ones, 
requirejs.config({
    "baseUrl": "js/lib",
    "paths": {
      "app": "../app",
      "jquery": "jquery-1.10.2",
      "datepicker" : "bootstrap-datepicker",
      "datepicker.zh-CN" : "locales/bootstrap-datepicker.zh-CN"
    },
    "shim": {
        "jquery.alpha": ["jquery"],
        "jquery.beta": ["jquery"],
        "bootstrap" : ["jquery"], // 3.0.3
        "datepicker" : ["jquery"], // 1.2.0
        "datepicker.zh-CN" : ["datepicker"]
    }
});

// Load the main app module to start the app
requirejs(["app/main"]);
