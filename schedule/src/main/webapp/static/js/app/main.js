define(["jquery", "bootstrap", "datepicker", "datepicker.zh-CN"], function($) {
    //the jquery.alpha.js and jquery.beta.js plugins have been loaded.
    $(function() {

        $('div#my-bootstrap-datepicker').datepicker({
		    todayBtn: "linked",
		    language: "zh-CN"
		});
        
        var start = $('input#start-date').datepicker({
        	format: 'yyyy-mm-dd',
        	onRender: function(date) {
        	    return date.valueOf() < new Date().valueOf() ? 'disabled' : '';
        	  }
        }).on('changeDate', function(ev) {
        	  if (ev.date.valueOf() > end.date.valueOf()) {
        	    var newDate = new Date(ev.date);
        	    newDate.setDate(newDate.getDate() + 1);
        	    end.setValue(newDate);
        	  }
        	  start.hide();
        	  $('input#end-date')[0].focus();
        	}).data('datepicker');
        
    	var end = $('input#end-date').datepicker({
    	  format: 'yyyy-mm-dd',
    	  onRender: function(date) {
    	    return date.valueOf() <= start.date.valueOf() ? 'disabled' : '';
    	  }
    	}).on('changeDate', function(ev) {
    	  end.hide();
    	}).data('datepicker');

    });
});
