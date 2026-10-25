// Karma configuration
// Generated on Fri Oct 16 2026 20:48:03 GMT-0300 (hora de verano de Chile)

module.exports = function(config) {
  config.set({
    frameworks: ["jasmine", "karma-typescript"],

    files: [
      { pattern: "src/**/*.ts" },
      { pattern: "src/**/*.tsx" }
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
      bundlerOptions: {
        // Solo los archivos .spec son "puntos de entrada" (lo que se ejecuta)
        entrypoints: /\.spec\.(ts|tsx)$/
      }
    },

    reporters: ["progress", "karma-typescript"],
    browsers: ["ChromeHeadless"],
    singleRun: false
  });
}
