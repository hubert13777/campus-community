function like(button, entityType, entityId) {
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType": entityType, "entityId": entityId},
        function (data){
            data=$.parseJSON(data);
            if(data.code==0){
                $(button).children("b").text(data.likeStatus==1?'已赞':'赞');
                $(button).children("i").text(data.likeCount);
            }else{  //失败了
                alert(data.msg);
            }
        }
    );
}