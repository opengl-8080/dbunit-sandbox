
public class CreateSetUp {
    // 標準出力にリダイレクトしてファイルを生成する
    public static void main(String[] args) {
        final String value = repeat('a', 1024);
        System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        System.out.println("<dataset>");
        for (int i=0; i<500000; i++) {
            System.out.println("<test_table id=\"" + (i+1) + "\" value=\"" + value + "\" />");
        }
        System.out.println("</dataset>");
    }

    private static String repeat(char c, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i=0; i<n; i++) {
            sb.append(c);
        }
        return sb.toString();
    }
}