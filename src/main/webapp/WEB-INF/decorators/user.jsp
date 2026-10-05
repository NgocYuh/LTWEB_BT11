<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
    <title><sitemesh:write property="title"/> | HNHBOOKSTORE</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/site.css">
    <script src="${pageContext.request.contextPath}/assets/js/site.js" defer></script>
    <sitemesh:write property="head"/>
</head>
<body data-layout="user">
<a class="skip-link" href="#main">Đến nội dung</a>
<%@ include file="../fragments/header.jspf" %>
<main id="main" class="container main-content"><sitemesh:write property="body"/></main>
<%@ include file="../fragments/footer.jspf" %>
</body></html>
