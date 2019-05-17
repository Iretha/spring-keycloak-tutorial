<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Hello, ${name}!</title>
<%--    <link href="/static/css/style.css" rel="stylesheet">--%>
</head>
<body>
<h2 style="color: darkcyan;">Hello, ${name}!</h2>

<form method="post" action="/logout-from-keycloak">
    <input type="submit" value="Logout"/>
</form>

<%--<script src="/static/js/user.js"></script>--%>
</body>
</html>