package com.mx.tecdesoftware.Pendix.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.net.URI;

@Component
public class BrowserLauncher {

    private final Environment environment;

    public BrowserLauncher(Environment environment) {
        this.environment = environment;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void openSwagger() {
        boolean autoOpen = environment.getProperty(
                "app.browser.auto-open",
                Boolean.class,
                false
        );

        if (!autoOpen) {
            return;
        }

        String port = environment.getProperty("server.port", "8080");
        String swaggerUrl =
                "http://localhost:" + port + "/swagger-ui.html";

        Thread browserThread = new Thread(() -> {
            try {
                System.out.println(
                        "Abriendo Swagger UI en: " + swaggerUrl
                );

                if (Desktop.isDesktopSupported()
                        && Desktop.getDesktop().isSupported(
                                Desktop.Action.BROWSE
                        )) {

                    Desktop.getDesktop().browse(
                            URI.create(swaggerUrl)
                    );

                } else {
                    new ProcessBuilder(
                            "xdg-open",
                            swaggerUrl
                    ).start();
                }

            } catch (Exception exception) {
                System.err.println(
                        "No se pudo abrir el navegador automáticamente."
                );
                System.err.println(
                        "Abre manualmente: " + swaggerUrl
                );
                System.err.println(
                        "Detalle: " + exception.getMessage()
                );
            }
        });

        browserThread.setName("swagger-browser-launcher");
        browserThread.setDaemon(true);
        browserThread.start();
    }
}
