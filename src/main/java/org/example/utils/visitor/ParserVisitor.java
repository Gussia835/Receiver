package org.example.utils.visitor;

import java.nio.charset.Charset;
import java.nio.file.Path;

public interface ParserVisitor {
    boolean visit(String line);


}
