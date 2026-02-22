<%@ page contentType="text/html;charset=UTF-8" %>

<%
    String error = (String) request.getAttribute("error");
%>
<div class="auth-scene">
    <div class="app-card" style="background: rgba(255,255,255,0.98); padding: 2.5rem; border-radius: 16px; box-shadow: 0 30px 80px rgba(0,0,0,0.25); width: 100%; max-width: 420px;">

        <div class="app-logo-ring" style="position: relative; width: 70px; height: 70px; margin-inline: auto; margin-bottom: 2rem; display: flex; align-items: center; justify-content: center; border-radius: 50%; background: linear-gradient(135deg, rgba(10,157,192,0.12), rgba(0,78,110,0.08)); border: 1px solid rgba(10,157,192,0.22);">

            <img src="${pageContext.request.contextPath}/assets/img/logo.png"
                 alt="Logo"
                 style="width: 70px; height: 70px; object-fit: contain;" />
        </div>

        <h1 class="text-center mb-1" style="font-family:
        'Playfair Display', serif; font-size: 32px; font-weight: 600;
        color: #002B3E; letter-spacing: -0.5px;">Welcome</h1>
        <p class="text-center text-muted mb-4" style="font-size: 14px;">Sign in to Ocean View account</p>

        <% if (error != null) { %>
        <div class="app-alert--error" style="background: #FFE5E5; border-left: 4px solid #C03929; padding: 12px 16px; border-radius: 8px; margin-bottom: 20px; color: #7A1E15; font-size: 13px; text-align: center;">
            <i class="bi bi-exclamation-circle"></i> <%= error %>
        </div>
        <% } %>

        <form id="app-login-form" method="post" action="<%= request.getContextPath() %>/login" novalidate>
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}" />

            <div class="app-field mb-4" style="position: relative;">
                <label class="app-label" style="display: block; font-weight: 500; color: #004E6E; font-size: 13px; margin-bottom: 8px; letter-spacing: 0.3px;">Email Address</label>
                <input
                        type="text"
                        name="username"
                        id="app-username"
                        class="app-input form-control"
                        style="border: 1.5px solid #D4D8E0; border-radius: 10px; padding: 12px 14px; font-size: 14px; background: #FAFBFC; transition: all 0.2s ease; font-family: 'Jost', sans-serif;"
                        placeholder="Enter your username"
                        required>
            </div>

            <div class="app-field mb-3" style="position: relative;">
                <label class="app-label" style="display: block; font-weight: 500; color: #004E6E; font-size: 13px; margin-bottom: 8px; letter-spacing: 0.3px;">Password</label>
                <div style="position: relative;">
                    <input
                            type="password"
                            name="password"
                            id="app-password"
                            class="app-input form-control"
                            style="border: 1.5px solid #D4D8E0; border-radius: 10px; padding: 12px 14px; font-size: 14px; background: #FAFBFC; transition: all 0.2s ease; font-family: 'Jost', sans-serif; padding-right: 44px;"
                            placeholder="Enter password"
                            required>
                    <button
                            type="button"
                            class="auth-eye"
                            data-target="app-password"
                            style="position: absolute; right: 10px; top: 50%; transform: translateY(-50%); padding: 6px 10px; color: rgba(0,78,110,0.5); background: none; border: none; cursor: pointer; font-size: 18px; transition: color 0.2s ease;">
                        <i class="bi bi-eye-slash"></i>
                    </button>
                </div>
            </div>

<%--            <div class="d-flex justify-content-between align-items: center; mb-4" style="font-size: 13px;">--%>
<%--    &lt;%&ndash;            <div class="form-check" style="position: relative;">&ndash;%&gt;--%>
<%--    &lt;%&ndash;                <input&ndash;%&gt;--%>
<%--    &lt;%&ndash;                        class="app-check-input form-check-input"&ndash;%&gt;--%>
<%--    &lt;%&ndash;                        type="checkbox"&ndash;%&gt;--%>
<%--    &lt;%&ndash;                        name="remember"&ndash;%&gt;--%>
<%--    &lt;%&ndash;                        id="app-remember"&ndash;%&gt;--%>
<%--    &lt;%&ndash;                        style="position: absolute; opacity: 0; width: 0; height: 0; cursor: pointer;">&ndash;%&gt;--%>
<%--    &lt;%&ndash;                <label class="form-check-label" style="display: flex; align-items: center; gap: 8px; cursor: pointer; user-select: none;">&ndash;%&gt;--%>
<%--    &lt;%&ndash;                    <span class="app-check-box" style="display: inline-flex; align-items: center; justify-content: center; width: 18px; height: 18px; border: 1.5px solid #D4A853; border-radius: 4px; background: #FFF; transition: all 0.2s ease;">&ndash;%&gt;--%>
<%--    &lt;%&ndash;                        <i class="bi bi-check" style="color: #FFF; font-size: 13px; opacity: 0; transform: scale(0.5); transition: all 0.2s ease;"></i>&ndash;%&gt;--%>
<%--    &lt;%&ndash;                    </span>&ndash;%&gt;--%>
<%--    &lt;%&ndash;                    <span style="color: #004E6E; font-weight: 500;">Remember me</span>&ndash;%&gt;--%>
<%--    &lt;%&ndash;                </label>&ndash;%&gt;--%>
<%--    &lt;%&ndash;            </div>&ndash;%&gt;--%>
<%--                <a href="#" class="app-forgot" style="color: #0A9DC0; text-decoration: none; font-weight: 500; position: relative; transition: color 0.2s ease;">Forgot password?</a>--%>
<%--            </div>--%>

            <button
                    type="submit"
                    class="app-submit btn w-100"
                    style="background: linear-gradient(135deg, #0A9DC0 0%, #007BA3 55%, #005F82 100%); color: #FFF; border: none; border-radius: 10px; padding: 12px 16px; font-weight: 600; font-size: 15px; letter-spacing: 0.5px; box-shadow: 0 12px 32px rgba(10,157,192,0.35); transition: all 0.2s ease; cursor: pointer; position: relative; overflow: hidden;">
                Sign In
            </button>

        </form>

            <%--    <p class="text-center mt-4" style="font-size: 13px; color: #004E6E;">--%>
            <%--        Don't have an account?--%>
            <%--        <a href="<%= request.getContextPath() %>/register" style="color: #0A9DC0; text-decoration: none; font-weight: 600; transition: color 0.2s ease;">Sign up here</a>--%>
            <%--    </p>   --%>

    </div>
</div>
