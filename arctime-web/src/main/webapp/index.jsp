
index.jsp

<%= System.currentTimeMillis() %>

<br/>
User: <%= request.getRemoteUser() %><br/>

<% for (com.arcblaze.arctime.model.Role role : com.arcblaze.arctime.model.Role.values()) { %>
  <%= role.name() %>? <%= request.isUserInRole(role.name()) %><br/>
<% } %>

