package io.github.i49.unite.schema;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SchemaGenerator {
    
    private static final String TEMPLATE_NAME = "create-schema.template";
    
    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\$\\{(\\w+)\\}");

    public void generate() throws IOException {
        Path templatePath = Paths.get(TEMPLATE_NAME);
        List<String> lines = Files.readAllLines(templatePath);
        DirectoryStream<Path> directory = Files.newDirectoryStream(Paths.get("."), "*.properties");
        directory.forEach(path->writeSchema(path, lines));
    }
    
    private void writeSchema(Path path, List<String> lines) {
        try {
            doWriteSchema(path, lines);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    private void doWriteSchema(Path path, List<String> lines) throws IOException {
        Properties props = loadProperties(path);
        List<String> replaced = replace(lines, props);
        String fileName = path.getFileName().toString();
        int lastIndex = fileName.lastIndexOf(".");
        String suffix = fileName.substring(0, lastIndex);
        Path outputPath = Paths.get("create-schema-" + suffix + ".sql");
        System.out.println("Writing " + outputPath);
        Files.write(outputPath, replaced);
    }
    
    private Properties loadProperties(Path path) throws IOException {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(path)) {
            props.load(in);
        }
        return props;
    }
    
    private List<String> replace(List<String> lines, Properties props) {
        return lines.stream()
            .map(line->{
                StringBuffer buffer = new StringBuffer();
                Matcher m = TOKEN_PATTERN.matcher(line);
                while (m.find()) {
                    String key = m.group(1);
                    String value = props.getProperty(key);
                    m.appendReplacement(buffer, value);
                }
                m.appendTail(buffer);
                return buffer.toString();
            })
            .collect(Collectors.toList());
    }
    
    public static void main(String[] args) {
        try {
            new SchemaGenerator().generate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
