package io.github.i49.unite.mojos;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Mojo which generates resources from template files.
 * 
 * @goal template
 * @phase generate-resources
 */
public class TemplateMojo extends AbstractMojo {
  
    /**
     * @parameter property="outputDirectory" default-value="src/main/resources"
     */
    private File outputDirectory;
    
    /**
     * @parameter property="templateDirectory" default-value="src/main/resources"
     */
    private File templateDirectory;
    
    /**
     * @parameter property="propertiesDirectory" default-value="src/main/resources"
     */
    private File propertiesDirectory;

    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\$\\{(\\w+)\\}");
    
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().debug("outputDirectory: " + this.outputDirectory.toString());
        getLog().debug("templateDirectory: " + this.templateDirectory.toString());
        getLog().debug("propertiesDirectory: " + this.propertiesDirectory.toString());
        
        try {
            Map<String, Properties> props = findProperties(propertiesDirectory.toPath());
            getLog().debug("properties: " + props.keySet());
            
            generateResources(templateDirectory.toPath(), outputDirectory.toPath(), props);

        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
    
    private Map<String, Properties> findProperties(Path directory) throws IOException {
        Map<String, Properties> map = new HashMap<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.properties")) {
            for (Path path: stream) {
                if (Files.isDirectory(path)) {
                    continue;
                }
                String key = path.getFileName().toString();
                key = key.substring(0, key.lastIndexOf('.'));
                map.put(key, loadProperties(path));
            }
        }
        return map;
    }
    
    private void generateResources(Path source, Path target, Map<String, Properties> props) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String name = file.getFileName().toString();
                if (name.endsWith(".template")) {
                    generateResources(source, target, file, props);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
    private static Properties loadProperties(Path path) throws IOException {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(path)) {
            props.load(in);
        }
        return props;
    }
    
    private void generateResources(Path source, Path target, Path template, Map<String, Properties> props) throws IOException {
        getLog().debug("Template: " + template.toString());
   
        String name = template.getFileName().toString();
        name = name.substring(0, name.lastIndexOf('.'));

        Path targetDir = target.resolve(source.relativize(template.getParent()));
        Files.createDirectories(targetDir);
        
        List<String> lines = Files.readAllLines(template);
        for (String specifier: props.keySet()) {
            String targetName = buildTargetName(name, specifier);
            Path targetFile = targetDir.resolve(targetName);
            generateResource(targetFile, lines, props.get(specifier));
        }
    }
    
    private void generateResource(Path target, List<String> lines, Properties props) throws IOException {
        getLog().info("Generating " + target.toString() + " ...");
        List<String> replaced = replace(lines, props);
        Files.write(target, replaced);
    }
  
    private static List<String> replace(List<String> lines, Properties props) {
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
    
    private static String buildTargetName(String sourceName, String specifier) {
        StringBuilder b = new StringBuilder();
        int index = sourceName.lastIndexOf('.');
        if (index >= 0) {
            b.append(sourceName.substring(0, index))
             .append("-")
             .append(specifier)
             .append(sourceName.substring(index));
        } else {
            b.append(sourceName)
             .append("-")
             .append(specifier);
        }
        return b.toString();
    }
}
