function getCurrentCode(){
    var lastCode = $('tbody tr:last-child td:first-child').text();
    var newCode = 'C00'+(parseInt(lastCode.substring(2,4))+1);
    return newCode;
}

$(document).ready(function () {

    $.ajax({
        method:"GET",
        url:"http://localhost:8080/customer",
        async:true
    })
        .done(function (customers,textStatus,jqXHR) {

            for (var i=0; i<customers.length;i++){
                $('tbody').append('<tr><td>'+customers[i].id+'</td><td>'+customers[i].name+'</td><td>'+customers[i].address+'</td><td><img src="https://img.icons8.com/material/24/000000/delete.png"></td></tr>');

                $('tbody tr td img').off('click');
                $('tbody tr td img').click(function () {
                    var id = $(this).parents('tr').find('td:first-child').html();
                    console.log(id);
                    $(this).parents('tr').remove();
                    $.ajax({
                        method:"DELETE",
                        url:"http://localhost:8080/customer",
                        async:true,
                        dataType: 'json',
                        contentType: 'application/json',
                        data:JSON.stringify({
                            id:id
                        })
                    }).done(function (data) {
                        alert("Customer Deleted Successfully");
                    }).fail(function (error) {
                        console.log(error);
                    });
                });

                //update
                $('tbody tr').off("click");
                $('tbody tr').click(function () {
                    var clone = $('#addCustomer').clone(true);
                    $('#addCustomer').replaceWith('<button id="updateCustomer" type="button" class="btn btn-primary">Update Customer</button>');

                    var back = $(this);
                    var id = $(this).find('td:nth-child(1)').html();
                    var name = $(this).find('td:nth-child(2)').html();
                    var address = $(this).find('td:nth-child(3)').html();

                    $('#exampleInputName').val(name);
                    $('#exampleInputAddress').val(address);
                    $('#exampleInputID').val("C"+id);

                    $('#updateCustomer').click(function () {

                        var valid = true;

                        if ($.trim($('#exampleInputName').val()).length ===0){
                            $('#exampleInputName').css('box-shadow','1px 2px 5px  #b80009');
                            valid = false;
                        }
                        if ($.trim($('#exampleInputAddress').val()).length ===0){
                            $('#exampleInputAddress').css('box-shadow','1px 2px 5px  #b80009');
                            valid = false;
                        }

                        if (valid){
                            var name = $('#exampleInputName').val();
                            var id = $('#exampleInputID').val();
                            var address = $('#exampleInputAddress').val();

                            $.ajax({
                                method:"PUT",
                                url:"http://localhost:8080/customer/"+id.substring(1),
                                async:true,
                                contentType: 'application/json',
                                data:JSON.stringify({
                                    address:address,
                                    name:name
                                })
                            }).done(function (data) {
                                alert("Customer Updated Successfully");
                            }).fail(function (error) {
                                console.log(error);
                            });

                            back.find('td:nth-child(2)').replaceWith('<td>'+name+'</td>');
                            back.find('td:nth-child(3)').replaceWith('<td>'+address+'</td>');

                            $('#updateCustomer').replaceWith(clone);
                            $('#addCustomer').trigger('change');

                            var id = $('tbody tr:last-child td:first-child').text();
                            var currentId = 'C00'+(++id);
                            console.log(currentId);
                            $('#exampleInputID').val('');
                            $('#exampleInputID').val(currentId);
                            $('#exampleInputName').val('');
                            $('#exampleInputAddress').val('');
                        }

                    });
                });


            }
            var id = $('tbody tr:last-child td:first-child').text();
            var currentId = 'C00'+(++id);
            $('#exampleInputID').val(currentId);
        })
        .fail(function (jqXHR,textStatus,errorThrown) {
            console.log(errorThrown);
        });

});

$('#exampleInputID').change(function () {
    $('#exampleInputID').css('box-shadow','1px 2px 5px  #0062cc');
});
$('#exampleInputName').change(function () {
    $('#exampleInputName').css('box-shadow','1px 2px 5px  #0062cc');
});
$('#exampleInputAddress').change(function () {
    $('#exampleInputAddress').css('box-shadow','1px 2px 5px  #0062cc');
});

$('#addCustomer').click(function () {
    var valid = true;
    
    if ($.trim($('#exampleInputName').val()).length ===0){
        $('#exampleInputName').css('box-shadow','1px 2px 5px  #b80009');
        valid = false;
    }
    if ($.trim($('#exampleInputAddress').val()).length ===0){
        $('#exampleInputAddress').css('box-shadow','1px 2px 5px  #b80009');
        valid = false;
    }
    if (valid){
        var name = $('#exampleInputName').val();
        var id = $('#exampleInputID').val();
        var address = $('#exampleInputAddress').val();

        $.ajax({
            method:"POST",
            url:"http://localhost:8080/customer",
            async:true,
            contentType: 'application/json',
            data:JSON.stringify({
                id:id.substring(1),
                address:address,
                name:name
            })
        }).done(function (data) {
            alert("Customer Saved Successfully");

            $('tbody').append('<tr><td>'+id+'</td><td>'+name+'</td><td>'+address+'</td><td><img src="https://img.icons8.com/material/24/000000/delete.png"></td></tr>');

            var id1 = $('tbody tr:last-child td:first-child').text();
            var currentId = 'C00'+(parseInt(id1.substring(1))+1);
            $('#exampleInputID').val('');
            $('#exampleInputID').val(currentId);
            $('#exampleInputName').val('');
            $('#exampleInputAddress').val('');

            $('tbody tr td img').off('click');
            $('tbody tr td img').click(function () {
                var id = $(this).parents('tr').find('td:first-child').html();
                console.log(id);
                $(this).parents('tr').remove();
                $.ajax({
                    method:"DELETE",
                    url:"http://localhost:8080/customer",
                    async:true,
                    dataType: 'json',
                    contentType: 'application/json',
                    data:JSON.stringify({
                        id:id
                    })
                }).done(function (data) {
                    alert("Customer Deleted Successfully");
                }).fail(function (error) {
                    console.log(error);
                });
            });

            //update
            //$('tbody tr').off("click");
            $('tbody tr:last-child').click(function () {
                var clone = $('#addCustomer').clone(true);
                $('#addCustomer').replaceWith('<button id="updateCustomer" type="button" class="btn btn-primary">Update Customer</button>');

                var back = $(this);
                var id = $(this).find('td:nth-child(1)').html();
                var name = $(this).find('td:nth-child(2)').html();
                var address = $(this).find('td:nth-child(3)').html();

                $('#exampleInputName').val(name);
                $('#exampleInputAddress').val(address);
                $('#exampleInputID').val(id);

                $('#updateCustomer').click(function () {

                    var valid = true;

                    if ($.trim($('#exampleInputName').val()).length ===0){
                        $('#exampleInputName').css('box-shadow','1px 2px 5px  #b80009');
                        valid = false;
                    }
                    if ($.trim($('#exampleInputAddress').val()).length ===0){
                        $('#exampleInputAddress').css('box-shadow','1px 2px 5px  #b80009');
                        valid = false;
                    }

                    if (valid){
                        var name = $('#exampleInputName').val();
                        var id = $('#exampleInputID').val();
                        var address = $('#exampleInputAddress').val();

                        $.ajax({
                            method:"PUT",
                            url:"http://localhost:8080/customer/"+id.substring(1),
                            async:true,
                            contentType: 'application/json',
                            data:JSON.stringify({
                                address:address,
                                name:name
                            })
                        }).done(function (data) {
                            alert("Customer Updated Successfully");
                        }).fail(function (error) {
                            console.log(error);
                        });

                        back.find('td:nth-child(2)').replaceWith('<td>'+name+'</td>');
                        back.find('td:nth-child(3)').replaceWith('<td>'+address+'</td>');

                        $('#updateCustomer').replaceWith(clone);
                        $('#addCustomer').trigger('change');

                        var id = $('tbody tr:last-child td:first-child').text();
                        var currentId = 'C00'+(parseInt(id.substring(1))+1);
                        console.log(currentId);
                        $('#exampleInputID').val('');
                        $('#exampleInputID').val(currentId);
                        $('#exampleInputName').val('');
                        $('#exampleInputAddress').val('');
                    }

                });
            });

        }).fail(function (error) {
            console.log(error);
        });

    }
});