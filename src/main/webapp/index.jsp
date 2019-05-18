<%--如果 不加这句中文显示会乱码--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<html>
<body>
<h2>Hello World!</h2>

<%--通过自己写的jsp来进行文件上传自测--%>
springmvc上传文件
<form name = "form1" action = "/manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" value="springmvc上传文件" />
</form>

富文本图片上传文件
<form name="form2" action="/manage/product/rich_img_upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="富文本图片上传文件">
</form>
</body>
</html>
