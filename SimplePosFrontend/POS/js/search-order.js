$(document).ready(function () {

    $.ajax({
        method:"GET",
        url:"http://localhost:8080/placeorder",
        async:true
    }).done(function (response) {

    })

    for (var i=0;i<order.length;i++){
        $('tbody').append('<tr><td>'+order[i].oid+'</td><td>'+order[i].cusId+'</td><td>'+order[i].date+'</td><td>'+order[i].total+'</td></tr>');
    }
});

$('#search').keyup(function () {

      $("tbody tr").hide();
    // console.log($(this).val())
    // $('td:contains('+$(this).val()+')').parents('tr').show();

    var key = $(this).val();
    $('tbody tr td').each(function () {
        var nw1 = $(this).text().substring(0,key.length);
        //console.log(nw,key);
        if (nw1 === key){
            $(this).parents('tr').show();
        }

    });
});