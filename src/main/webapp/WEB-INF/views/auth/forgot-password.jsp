<%@ page contentType="text/html;charset=UTF-8" %>

<%
    String error = (String) request.getAttribute("error");
    String message = (String) request.getAttribute("message");
%>

<div class="app-card" style="background: rgba(255,255,255,0.98); padding: 2.5rem; border-radius: 16px; box-shadow: 0 30px 80px rgba(0,0,0,0.25); width: 100%; max-width: 420px;">

    <h1 class="text-center mb-1" style="font-family: 'Playfair Display', serif; font-size: 30px; font-weight: 600; color: #002B3E;">Forgot Password</h1>
    <p class="text-center text-muted mb-4" style="font-size: 14px;">Enter your account email to receive a reset link.</p>

    <% if (error != null) { %>
    <div class="app-alert--error" style="background: #FFE5E5; border-left: 4px solid #C03929; padding: 12px 16px; border-radius: 8px; margin-bottom: 20px; color: #7A1E15; font-size: 13px; text-align: center;">
        <i class="bi bi-exclamation-circle"></i> <%= error %>
    </div>
    <% } %>

    <% if (message != null) { %>
    <div style="background: #E8F7EE; border-left: 4px solid #2E8B57; padding: 12px 16px; border-radius: 8px; margin-bottom: 20px; color: #1B5E20; font-size: 13px; text-align: center;">
        <i class="bi bi-check-circle"></i> <%= message %>
    </div>
    <% } %>

    <form method="post" action="<%= request.getContextPath() %>/forgot-password" novalidate>
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />

        <div class="app-field mb-4">
            <label class="app-label" style="display: block; font-weight: 500; color: #004E6E; font-size: 13px; margin-bottom: 8px;">Email Address</label>
            <input
                    type="email"
                    name="email"
                    class="app-input form-control"
                    style="border: 1.5px solid #D4D8E0; border-radius: 10px; padding: 12px 14px; font-size: 14px; background: #FAFBFC;"
                    placeholder="Enter your email address"
                    required>
        </div>

        <button type="submit" class="app-submit btn w-100" style="background: linear-gradient(135deg, #0A9DC0 0%, #007BA3 55%, #005F82 100%); color: #FFF; border: none; border-radius: 10px; padding: 12px 16px; font-weight: 600; font-size: 15px;">
            Send Reset Link
        </button>

        <p class="text-center mt-3" style="font-size: 13px;">
            <a href="<%= request.getContextPath() %>/login" style="color: #0A9DC0; text-decoration: none; font-weight: 600;">Back to Login</a>
        </p>
    </form>
</div>