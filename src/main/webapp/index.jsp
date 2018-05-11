<html>
<body>
<h2>Hello World!</h2>

<img src="http://file.neusoft.com/group1/M00/00/00/CgENTVrrtkCAD9lkAAA8e7dSQCI69.jpeg" />

<h2>文件上传</h2>
<form action="fastdfs/upload/file/sample" enctype="multipart/form-data" method="post">
    <table>
        <tr>
            <td>文件描述:</td>
            <td><input type="text" name="description"></td>
        </tr>
        <tr>
            <td>请选择文件:</td>
            <td><input type="file" name="file"></td>
        </tr>
        <tr>
            <td><input type="submit" value="上传"></td>
        </tr>
    </table>
</form>




<input type="file" id="file" name="file"  style="width: 180px;" onchange="uploadFile(this)" />
<input type="text" id="videoFileId" style="border: none;display:none;width:20%;" onclick="this.style.display='none';document.getElementById('file').style.display='';">
<input type="hidden" id="videoType" name="imageType"/>


</body>
<script type="text/javascript">
    function uploadFile(formTag){
        var fileInput = document.getElementById("file");
        var videoInput = document.getElementById("videoFileId");
        var filename=fileInput.value;
        var uploadFileName=filename.substring(filename.lastIndexOf("\\")+1,filename.length);
        var url = "http://localhost:8080/fastdfs/download/image";
        $.ajaxFileUpload({
                             url : url,
                             type:"Post",
                             dataType:'json',
                             fileElementId : "file",// 文件选择框的id属性
                             success : function(data, status) {
                                 $(videoInput).css("display","");
                                 document.getElementById("file").style.display="none";
                                 videoInput.value=uploadFileName;
                             },
                             error : function(XMLHttpRequest, textStatus, errorThrown) {
                                 $(videoInput).css("display","none");
                                 fileInput.style.display="";
                                 $.alert({
                                             title: '温馨提示',
                                             content: "上传失败！",
                                             confirmButton: '确定',
                                             confirmButtonClass: 'btn-primary',
                                             animation: 'scale',
                                             confirm: function () {
                                             }
                                         });
                             }
                         });
    }
</script>

</html>
