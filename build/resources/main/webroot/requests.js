var api_url = "http://localhost:8888/api/v12/services";

window.setInterval(function(){
    $("tbody tr").remove();
    $.ajax({
        type: 'GET',
        contentType: 'application/json',
        url: api_url,
        dataType: 'json',
        success: (data) => {
          $.each(data,function(key,value){
            if(value.status == 1) {
              value.status = '<span class="status text-success">&bull;</span> OK'
            }else {
              value.status = '<span class="status text-danger">&bull;</span> FAIL'
            }
            $('tbody').append('<tr><td>'+(key+1)+'</td><td class="name">'+value.name+'</td><td>'+value.url+'</td><td>'+value.status+'</td><td>'+value.lastUpdate +'</td><td><a href="#" class="delete" title="Delete" data-toggle="tooltip"><i class="fa fa-trash-o"></i></a></td></tr>');
          })
        },
        error: (data) => {
            console.log(data);
        }
    });


}, 10000);

$("tbody").on('click', '.delete', function(e) {
  $.ajax({
    type: 'DELETE',
    contentType: 'application/json',
    url: api_url+"/" + $(this).closest('tr').find('.name')[0].outerText,
    dataType : 'json',
    success : (data) => {
      $(this).closest('tr').fadeTo(400, 0,  () => {
         $(this).remove();
      });
    },
    error : (data) => {
      console.log(data)
    }
  });

})

$( "#service-form" ).submit(function( event ) {
  event.preventDefault();
  var $form = $( this );

    var data = { "name": $( "#name" ).val(), "url": $( "#url" ).val() }
    if(!IsURL(data.url)) {
      console.log("Link is not a valid URL")
      return;
    }

    $.ajax({
        type: 'POST',
        contentType: 'application/json',
        url: api_url,
        dataType : 'json',
        data : JSON.stringify(data),
        success : (dataReturned) => {
        console.log(dataReturned)
            $('tbody:last-child').append('<tr><td>'+($('tr').length+1)+'</td><td class="name">'+data.name+'</td><td>'+data.url+'</td><td>'+'Just now'+'</td><td>'+'<span class="status text-danger">&bull;</span> FAIL' +'</td><td><a href="#" class="delete" title="Delete" data-toggle="tooltip"><i class="fa fa-trash-o"></i></a></td></tr>').hide().show('fast');
        },
        error : (data) => {
            console.log(data)
        }
    });
});
function IsURL(url) {

    var strRegex = "^((https|http|ftp|rtsp|mms)?://)"
        + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@
        + "(([0-9]{1,3}\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184
        + "|" // 允许IP和DOMAIN（域名）
        + "([0-9a-z_!~*'()-]+\.)*" // 域名- www.
        + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\." // 二级域名
        + "[a-z]{2,6})" // first level domain- .com or .museum
        + "(:[0-9]{1,4})?" // 端口- :80
        + "((/?)|" // a slash isn't required if there is no file name
        + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
     var re=new RegExp(strRegex);
     return re.test(url);
 }
