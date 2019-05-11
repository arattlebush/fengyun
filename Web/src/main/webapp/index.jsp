<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2017/5/20 0020
  Time: 14:15
  To change this template use File | Settings | File Templates.

--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<!--这句代码是为了适配手机、平板、桌面-->
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1,
    minimum-scale=1,user-scalable=no">
    <head>
        <title>风云来了</title>
        <link rel="stylesheet" href="css/bootstrap.css">
        <script src="js/jquery-3.2.1.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <style>
            *{
                padding: 0;
            }
            img{
                max-width: 100%;
            }
        </style>
        <script type="text/javascript">

        </script>

    </head>
        <body>

        <div class="container">
            <form class="form-signin">
                <h2 class="form-signin-heading">请登录云说平台</h2>
                <label for="inputMobile" class="sr-only">手机号</label>
                <input type="number" id="inputMobile" class="form-control" placeholder="请输入手机号" required autofocus>
                <label for="inputPassword" class="sr-only">Password</label>
                <input type="password" id="inputPassword" class="form-control" placeholder="请输入密码" required>
                <div class="checkbox">
                    <label>
                        <input type="checkbox" value="是否同意云说平台协议" id="cb">是否同意云说平台协议
                    </label>
                </div>
                <button class="btn btn-lg btn-primary btn-block" type="submit" id="login">
                    登录
                </button>
            </form>
        </div>
        </body>
</html>
