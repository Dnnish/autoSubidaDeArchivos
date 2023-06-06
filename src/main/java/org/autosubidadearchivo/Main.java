package org.autosubidadearchivo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.autosubidadearchivo.service.IFileService;
import org.autosubidadearchivo.service.impl.FtpService;

import java.io.IOException;

public class Main {
        private static final Logger log = LogManager.getLogger(Main.class);
        private static final int BATCH_SIZE = 100;
        private static final String DEFAULT_RELATIVE_PATH = "prueba";
        private static final String DEFAULT_BACKUP_PATH = "prueba/loaded/";

        public static void main(String[] args) throws IOException{
            //1. Init services {File}
            IFileService fileService = FtpService.builder()
                    .host("ipaqui")
                    .port(21)
                    .username("username")
                    .password("password")
                    .build();

            // Data Loader
            DataLoader dataLoader = DataLoader.builder()
                    .fileService(fileService)
                    .filePath(DEFAULT_RELATIVE_PATH)
                    .backUpPath(DEFAULT_BACKUP_PATH)
                    .batchSize(BATCH_SIZE)
                    .build();

            // Action
            log.info("Start");
            long start = System.currentTimeMillis();

            dataLoader.process();

            long finish = System.currentTimeMillis();
            long timeElapsed = finish - start;
            System.out.println("\nElapsed: " + timeElapsed + " ms\n");
            log.info("End");
        }
    }