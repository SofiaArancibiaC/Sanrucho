module.exports = function(config) {
  config.set({
    frameworks: ["jasmine", "karma-typescript"],

    files: [
      { pattern: "src/**/*.ts", included: true },
      { pattern: "src/**/*.tsx", included: true },
      { pattern: "public/**/*", included: false, served: true, watched: false }
    ],

    proxies: {
      "/static/": "/base/public/static/"
    },

    exclude: ["src/main.tsx"],

    preprocessors: {
      "src/**/*.ts": ["karma-typescript"],
      "src/**/*.tsx": ["karma-typescript"]
    },

    karmaTypescriptConfig: {
      tsconfig: "./tsconfig.json",
      compilerOptions: {
        jsx: "react-jsx",
        module: "CommonJS",
        moduleResolution: "Node"
      },
      transpileOnly: true,
      reports: {
        html: "coverage",
        "text-summary": "",
        json: "coverage/coverage-final.json"
      },
      coverageOptions: {
        instrumentation: true
      },
      bundlerOptions: {
        resolve: {
          alias: {
            axios: "src/test-mocks/axios.ts",
            sweetalert2: "src/test-mocks/sweetalert2.ts"
          }
        },
        ignore: ["jspdf", "html2canvas", "bootstrap", "react-bootstrap"]
      }
    },

    // Oculta todos los logs, WARN y ERROR que imprime el navegador durante los tests
    client: {
      captureConsole: false
    },

    reporters: ["spec", "karma-typescript"],
    browsers: ["ChromeHeadless"],
    singleRun: true
  });
};