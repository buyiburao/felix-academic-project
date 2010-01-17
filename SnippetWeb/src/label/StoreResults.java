package label;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StoreResults extends HttpServlet {

    private static final long serialVersionUID = -7565062504903388321L;

    public StoreResults() {
        super();
    }

    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        Map<String, String[]> params = request.getParameterMap();

        String user = params.get("user")[0];
        String query = params.get("query")[0];
        String url = params.get("url")[0];

        int rank = 0;
        while (params.containsKey("r_" + rank)) {
            String sentence = params.get("r_" + rank)[0];

            try {
                LabelManager.storeLabel(query, url, sentence, user, rank);
            } catch (Exception e) {
                throw new IOException(e);
            }
            rank++;
        }
        response.sendRedirect("label.jsp");
    }
}
