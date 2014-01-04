define(["jquery", "bootstrap", "datepicker", "datepicker.zh-CN"], function($) {
    //the jquery.alpha.js and jquery.beta.js plugins have been loaded.
    $(function() {

        $('div#my-bootstrap-datepicker').datepicker({
		    todayBtn: "linked",
		    language: "zh-CN"
		});

    });
});
