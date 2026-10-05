<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Quản trị · <sitemesh:write property="title"/> | HNHBOOKSTORE</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/site.css">
    <script src="${pageContext.request.contextPath}/assets/js/site.js" defer></script>
    <sitemesh:write property="head"/>
</head>
<body data-layout="admin">
<%@ include file="../fragments/header.jspf" %>
<div class="container admin-layout">
    <aside class="admin-sidebar"><span class="eyebrow">KHÔNG GIAN QUẢN TRỊ</span><a class="active" href="${pageContext.request.contextPath}/admin/books">Quản lý sách</a><a href="${pageContext.request.contextPath}/home">Xem thư viện</a></aside>
    <main id="main" class="main-content"><sitemesh:write property="body"/></main>
</div>
<%@ include file="../fragments/footer.jspf" %>
</body></html>
