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
        jsx: "react-jsx"
      },
      transpileOnly: true,
      bundlerOptions: {
        ignore: ["jspdf", "html2canvas", "sweetalert2", "bootstrap", "react-bootstrap"]
      }
    },

    // Oculta todos los logs, WARN y ERROR que imprime el navegador durante los tests
    client: {
      captureConsole: false
    },

    reporters: ["spec"],
    browsers: ["ChromeHeadless"],
    singleRun: true
  });
};