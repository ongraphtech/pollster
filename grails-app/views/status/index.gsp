<%@ page import="com.impl.pollster.Status" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="status">
    <g:set var="entityName" value="${message(code: 'status.label', default: 'Status')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body data-ng-app="statusScript" data-base-url="${createLink(uri: '/status/')}">
<a href="#list-status" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                             default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>

        <div data-ng-show="displayName">
            <li><a class="list" href="#list">Polls List</a></li>
            <li><a class="create" href="#create">New Poll</a></li>
        </div>
    </ul>
</div>

<div class="content" role="main" data-ng-view><div style="text-align: center">
    <g:img dir="images" file="ajax_loader.gif"></g:img>
</div>
</div>
</body>
</html>
