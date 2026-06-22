package com.hacc.api;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;

public class Main {

    public static void main(String[] args) {
        Quarkus.run(args);
    }

    public static class App implements QuarkusApplication {
      

        
        @Override
        public int run(String... args) throws Exception {
            
     
            return 0;
        }
    }

}
