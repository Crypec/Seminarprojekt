#include <fmt/format.h>
#include <iostream>
#include <string>
#include <sstream>
#include <cmath>
#include <nlohmann/json.hpp>

using nlohmann::json;

std::string __internal_get_input(std::string message) {
  fmt::print("{}", message);
  std::string input;
  std::getline(std::cin, input);
  return input;
}

int main() {
	start();
}
