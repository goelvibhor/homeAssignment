<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Tasty Search!</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script> </head>
    <script>
        function mySearch() {
            var queryTerms = [];
            for(var i = 1; i < 11; i++) {
                var q = document.forms["search-form"]["query"+i].value;
                if(q != null && q.trim().length  > 0){
                    q = q.trim();
                    q.replace(/\W/g, '');
                    document.forms["search-form"]["query"+i].value = q;
                    if(q.length > 0){
                        queryTerms.push(q);
                    }
                }
            }

            var index = document.forms["search-form"]["Index"].value;
            var url = "/search";
            if(index.trim() === "baseline"){
                url = "/search-baseline"
            }

            if(queryTerms.length > 0){
                xhr = new XMLHttpRequest();
                xhr.open("POST", url, true);
                xhr.setRequestHeader("Content-type", "application/json");
                xhr.onreadystatechange = function () {
                    if (xhr.readyState == 4 && xhr.status == 200) {
                        var json = JSON.parse(xhr.responseText);
                        var str = JSON.stringify(json, undefined, 4);
                        var printStr = syntaxHighlight(str);
                        var elem = document.createElement('pre').innerHTML = printStr;

                        $(".search-result").empty();

                        $(".search-result").append("<h4> Search Results </h4>");
                        $(".search-result").append(elem);
                    }
                }

                var data = JSON.stringify({"queryTerms": queryTerms}, undefined);
                xhr.send(data);
            }
        }

        function syntaxHighlight(json) {
            json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
            return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
                var cls = 'number';
                if (/^"/.test(match)) {
                    if (/:$/.test(match)) {
                        cls = 'key';
                    } else {
                        cls = 'string';
                    }
                } else if (/true|false/.test(match)) {
                    cls = 'boolean';
                } else if (/null/.test(match)) {
                    cls = 'null';
                }
                return '<span class="' + cls + '">' + match + '</span>';
            });
        }

    </script>
    <style>

        pre {outline: 1px solid #ccc; padding: 5px; margin: 5px; }
        .string { color: green; }
        .number { color: darkorange; }
        .boolean { color: blue; }
        .null { color: magenta; }
        .key { color: red; }
    </style>
</head>

<body>
<h2> Tasty Search!</h2>
<div id="mainWrapper">
    <div class="search-form">
        <form id="search-form" name="search-form" method="get" class="form-horizontal">
            Query 1: <input type="text" id="query1"><br />
            Query 2: <input type="text" id="query2"><br />
            Query 3: <input type="text" id="query3"><br />
            Query 4: <input type="text" id="query4"><br />
            Query 5: <input type="text" id="query5"><br />
            Query 6: <input type="text" id="query6"><br />
            Query 7: <input type="text" id="query7"><br />
            Query 8: <input type="text" id="query8"><br />
            Query 9: <input type="text" id="query9"><br />
            Query 10: <input type="text" id="query10"><br />
            Index type:
            <input type="radio" name="Index" value="optimised" checked> Optimised &nbsp; &nbsp; &nbsp;
            <input type="radio" name="Index" value="baseline"> baseline <br />
            <input type="button" onclick="mySearch()" value="Search">
        </form>
    </div>
    <div class="search-result" id="search-result">

    </div>
</div>

</body>
</html>