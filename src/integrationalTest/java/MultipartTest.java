//package java;
//
//import org.example.service.FileReceiverService;
//import org.example.service.impl.visitor.validator.EnrollValidator;
//import org.example.utils.filename.FileManager;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.TestPropertySource;
//
//import java.nio.file.Files;
//import java.nio.file.Path;
//
//@SpringBootTest
//@TestPropertySource(properties = {
//        "app.filespaths.process-dir=${java.io.tmpdir}/test-incoming",
//        "app.filespaths.target-dir=${java.io.tmpdir}/test-target"
//})
//public class MultipartTest {
//
//    @Autowired
//    private FileManager fileManager;
//
//    @Autowired
//    private EnrollValidator validator;
//
//    @MockBean
//    private FileReceiverService fileReceiverService;
//
//    @BeforeEach
//    void setUp() throws Exception {
//
//        Path processDir = fileManager.getProcessPath();
//        if (Files.exists(processDir)) {
//            Files.walk(processDir)
//                    .sorted((a, b) -> -a.compareTo(b))
//                    .forEach(path -> {
//                        try { Files.deleteIfExists(path); } catch (Exception ignored) {}
//                    });
//        }
//    }
//
//    @Test
//    public void validMultipartFileTest() {
//
//    }
//}
