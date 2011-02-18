package cscopefinder;

public class CscopeResult {

    public final String filename;
    public final int line;
    public final String namespace;
    public final String preview;

    public CscopeResult(String filename, String line, String namespace, String preview) {
        this.filename = filename;
        this.line = Integer.parseInt(line);
        this.namespace = namespace;
        this.preview = preview;
    }
}
