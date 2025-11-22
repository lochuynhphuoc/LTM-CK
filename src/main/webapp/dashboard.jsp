<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@ page contentType="text/html;charset=UTF-8" language="java" %>
            <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
                <html>

                <head>
                    <title>Dashboard - LTM Project</title>
                    <meta charset="UTF-8">
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            padding: 20px;
                            background-color: #f0f2f5;
                        }

                        .header {
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                            background: white;
                            padding: 10px 20px;
                            border-radius: 8px;
                            margin-bottom: 20px;
                        }

                        .content {
                            display: flex;
                            gap: 20px;
                        }

                        .form-card,
                        .list-card {
                            background: white;
                            padding: 20px;
                            border-radius: 8px;
                            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                        }

                        .form-card {
                            flex: 1;
                        }

                        .list-card {
                            flex: 2;
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
                            background: #1877f2;
                            color: white;
                            border: none;
                            border-radius: 4px;
                            cursor: pointer;
                        }

                        table {
                            width: 100%;
                            border-collapse: collapse;
                            margin-top: 10px;
                        }

                        th,
                        td {
                            padding: 10px;
                            text-align: left;
                            border-bottom: 1px solid #ddd;
                        }

                        .status-pending {
                            color: orange;
                            font-weight: bold;
                        }

                        .status-processing {
                            color: blue;
                            font-weight: bold;
                        }

                        .status-completed {
                            color: green;
                            font-weight: bold;
                        }

                        .status-failed {
                            color: red;
                            font-weight: bold;
                        }

                        /* Modal Styles */
                        .modal {
                            display: none;
                            position: fixed;
                            z-index: 1;
                            left: 0;
                            top: 0;
                            width: 100%;
                            height: 100%;
                            overflow: auto;
                            background-color: rgb(0, 0, 0);
                            background-color: rgba(0, 0, 0, 0.4);
                        }

                        .modal-content {
                            background-color: #fefefe;
                            margin: 10% auto;
                            padding: 20px;
                            border: 1px solid #888;
                            width: 80%;
                            max-width: 800px;
                            border-radius: 8px;
                            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
                        }

                        .close {
                            color: #aaa;
                            float: right;
                            font-size: 28px;
                            font-weight: bold;
                        }

                        .close:hover,
                        .close:focus {
                            color: black;
                            text-decoration: none;
                            cursor: pointer;
                        }

                        .preview-text {
                            white-space: pre-wrap;
                            /* Preserve whitespace and newlines */
                            font-family: monospace;
                            background: #f9f9f9;
                            padding: 15px;
                            border: 1px solid #eee;
                            border-radius: 4px;
                            max-height: 400px;
                            overflow-y: auto;
                        }

                        .view-btn {
                            background: #28a745;
                            padding: 5px 10px;
                            font-size: 12px;
                            margin-left: 5px;
                            width: auto;
                            /* Override default 100% width */
                        }
                    </style>
                </head>

                <body>

                    <div class="header">
                        <h2>Welcome, ${sessionScope.user.fullName}</h2>
                        <a href="logout" style="color: red; text-decoration: none;">Logout</a>
                    </div>

                    <div class="content">
                        <div class="form-card">
                            <h3>Plagiarism Checker</h3>
                            <p>Upload two files to check their content similarity.</p>
                            <p><i>Supported formats: .txt, .docx (Others will be read as raw text)</i></p>
                            <form action="dashboard" method="post" enctype="multipart/form-data">
                                <label>Source File (File gốc):</label>
                                <input type="file" name="sourceFile" required accept=".txt,.doc,.docx,.pdf">

                                <label>Target File (File cần kiểm tra):</label>
                                <input type="file" name="targetFile" required accept=".txt,.doc,.docx,.pdf">

                                <button type="submit">Check Similarity</button>
                            </form>
                        </div>

                        <div class="list-card">
                            <h3>Your Tasks</h3>
                            <table>
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Source Content</th>
                                        <th>Target Content</th>
                                        <th>Status</th>
                                        <th>Similarity (%)</th>
                                        <th>Time</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="task" items="${tasks}">
                                        <tr>
                                            <td>${task.id}</td>
                                            <td>
                                                <c:out
                                                    value="${task.url.length() > 20 ? task.url.substring(0, 20) : task.url}" />
                                                ...
                                                <button type="button" class="view-btn"
                                                    onclick="showPreview('Source Content', 'source-content-${task.id}')">View</button>
                                                <div id="source-content-${task.id}" style="display:none;">
                                                    <c:out value="${task.url}" />
                                                </div>
                                            </td>
                                            <td>
                                                <c:out
                                                    value="${task.keyword.length() > 20 ? task.keyword.substring(0, 20) : task.keyword}" />
                                                ...
                                                <button type="button" class="view-btn"
                                                    onclick="showPreview('Target Content', 'target-content-${task.id}')">View</button>
                                                <div id="target-content-${task.id}" style="display:none;">
                                                    <c:out value="${task.keyword}" />
                                                </div>
                                            </td>
                                            <td>
                                                <span class="status-${task.status.toLowerCase()}">${task.status}</span>
                                            </td>
                                            <td>${task.result}</td>
                                            <td>${task.createdAt}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <!-- The Modal -->
                    <div id="myModal" class="modal">
                        <div class="modal-content">
                            <span class="close" onclick="closeModal()">&times;</span>
                            <h3 id="modalTitle">File Content</h3>
                            <div id="modalText" class="preview-text"></div>
                        </div>
                    </div>

                    <script>
                        var modal = document.getElementById("myModal");
                        var modalTitle = document.getElementById("modalTitle");
                        var modalText = document.getElementById("modalText");

                        function showPreview(title, contentId) {
                            var content = document.getElementById(contentId).innerHTML;
                            modalTitle.innerText = title;
                            modalText.innerHTML = content; // Using innerHTML because c:out already escaped it
                            modal.style.display = "block";
                        }

                        function closeModal() {
                            modal.style.display = "none";
                        }

                        // When the user clicks anywhere outside of the modal, close it
                        window.onclick = function (event) {
                            if (event.target == modal) {
                                modal.style.display = "none";
                            }
                        }
                    </script>

                </body>

                </html>