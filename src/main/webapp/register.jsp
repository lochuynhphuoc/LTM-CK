<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <html>

    <head>
        <title>Register - LTM Project</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                display: flex;
                justify-content: center;
                align-items: center;
                height: 100vh;
                background-color: #f0f2f5;
            }

            .container {
                background: white;
                padding: 20px;
                border-radius: 8px;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                width: 300px;
            }

            input {
                width: 100%;
                padding: 10px;
                margin: 10px 0;
                border: 1px solid #ddd;
                border-radius: 4px;
                box-sizing: border-box;
            }

            button {
                width: 100%;
                padding: 10px;
                background: #42b72a;
                color: white;
                border: none;
                border-radius: 4px;
                cursor: pointer;
            }

            button:hover {
                background: #36a420;
            }

            .error {
                color: red;
                font-size: 14px;
                text-align: center;
            }

            .link {
                text-align: center;
                margin-top: 10px;
            }
        </style>
    </head>

    <body>
        <div class="container">
            <h2 style="text-align: center">Register</h2>
            <% if (request.getAttribute("error") !=null) { %>
                <p class="error">
                    <%= request.getAttribute("error") %>
                </p>
                <% } %>
                    <form action="register" method="post">
                        <input type="text" name="username" placeholder="Username" required>
                        <input type="password" name="password" placeholder="Password" required>
                        <input type="text" name="fullName" placeholder="Full Name" required>
                        <button type="submit">Sign Up</button>
                    </form>
                    <div class="link">
                        <a href="login">Already have an account?</a>
                    </div>
        </div>
    </body>

    </html>