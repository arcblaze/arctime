<!DOCTYPE html>
<html>
  <head>
    <title>ArcTime: Admin</title>
    <%@ include file="/ssi/meta.jspf" %>
    <link rel="stylesheet" href="/css/timesheet.css"/>
  </head>
  <body>
    <%@ include file="/ssi/header.jspf" %>
    <%@ include file="/ssi/user.jspf" %>
    <%@ include file="/ssi/scripts.jspf" %>

    <div id="container">
      <!-- This is where the UI components will be injected into the page. -->
      <div id="users-div"></div>
      <div id="companies-div"></div>

      <!-- Add the admin scripts. -->
      <script src="/js/src/util/io/ServerIO.js"></script>

      <script>
          // Do the post-startup activities.
          Ext.onReady(function() {
              // Build the UI.
          });
      </script>
    </div>

    <%@ include file="/ssi/footer.jspf" %>
  </body>
</html>
