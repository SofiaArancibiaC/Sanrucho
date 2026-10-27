module.exports = function(config) {
  config.set({
    frameworks: ["jasmine", "karma-typescript"],

    files: [
      { pattern: "src/**/*.ts", included: true },
      { pattern: "src/**/*.tsx", included: true }
    ],

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

    reporters: ["progress"],
    browsers: ["ChromeHeadless"],
    singleRun: true
  });
};