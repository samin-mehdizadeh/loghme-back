package ie.service.Filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
@WebFilter(urlPatterns = { "/restaurants","/DiscountFoods","/restaurantInfo/*","/search"})
public class DelayFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws  IOException, ServletException {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

}
