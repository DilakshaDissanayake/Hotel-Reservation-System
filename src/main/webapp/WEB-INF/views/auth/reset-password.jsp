<%@ page contentType="text/html;charset=UTF-8" %>

<%
    String error = (String) request.getAttribute("error");
    Boolean tokenValid = (Boolean) request.getAttribute("tokenValid");
    String resetToken = (String) request.getAttribute("resetToken");
    boolean showForm = tokenValid != null && tokenValid;
%>

<div class="app-card" style="background: rgba(255,255,255,0.98); padding: 2.5rem; border-radius: 16px; box-shadow: 0 30px 80px rgba(0,0,0,0.25); width: 100%; max-width: 420px;">

    <h1 class="text-center mb-1" style="font-family: 'Playfair Display', serif; font-size: 30px; font-weight: 600; color: #002B3E;">Reset Password</h1>
    <p class="text-center text-muted mb-4" style="font-size: 14px;">Set your new account password.</p>

    <% if (error != null) { %>
    <div class="app-alert--error" style="background: #FFE5E5; border-left: 4px solid #C03929; padding: 12px 16px; border-radius: 8px; margin-bottom: 20px; color: #7A1E15; font-size: 13px; text-align: center;">
        <i class="bi bi-exclamation-circle"></i> <%= error %>
    </div>
    <% } %>

    <% if (showForm) { %>
    <form method="post" action="<%= request.getContextPath() %>/reset-password" novalidate>
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />
        <input type="hidden" name="token" value="<%= resetToken %>">

        <div class="app-field mb-3">
            <label class="app-label" style="display: block; font-weight: 500; color: #004E6E; font-size: 13px; margin-bottom: 8px;">New Password</label>
            <input
                    type="password"
                    name="newPassword"
                    class="app-input form-control"
                    style="border: 1.5px solid #D4D8E0; border-radius: 10px; padding: 12px 14px; font-size: 14px; background: #FAFBFC;"
                    placeholder="At least 8 characters"
                    required>
        </div>

        <div class="app-field mb-4">
            <label class="app-label" style="display: block; font-weight: 500; color: #004E6E; font-size: 13px; margin-bottom: 8px;">Confirm Password</label>
            <input
                    type="password"
                    name="confirmPassword"
                    class="app-input form-control"
                    style="border: 1.5px solid #D4D8E0; border-radius: 10px; padding: 12px 14px; font-size: 14px; background: #FAFBFC;"
                    placeholder="Re-enter new password"
                    required>
        </div>

        <button type="submit" class="app-submit btn w-100" style="background: linear-gradient(135deg, #0A9DC0 0%, #007BA3 55%, #005F82 100%); color: #FFF; border: none; border-radius: 10px; padding: 12px 16px; font-weight: 600; font-size: 15px;">
            Update Password
        </button>
    </form>
    <% } else { %>
    <p class="text-center" style="font-size: 13px; color: #5C6670;">Request a new password reset link to continue.</p>
    <p class="text-center mt-2" style="font-size: 13px;">
        <a href="<%= request.getContextPath() %>/forgot-password" style="color: #0A9DC0; text-decoration: none; font-weight: 600;">Go to Forgot Password</a>
    </p>
    <% } %>
</div>