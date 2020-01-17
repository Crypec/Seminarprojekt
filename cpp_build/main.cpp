#include <fmt/format.h>
#include <iostream>
#include <string>
#include <sstream>
#include <nlohmann/json.hpp>

using nlohmann::json;

std::string __internal_get_input(std::string message) {
  fmt::print("{}", message);
  std::string input;
  std::getline(std::cin, input);
  return input;
}
struct Person {
    std::string name;
    double alter;
};

void Start() {
std::string a = "test";

auto foo = 2.0;

if (foo == 2.0) {
auto bar = __internal_get_input("was soll bar sein: ");

fmt::print("{}",bar);
}

fmt::print("test",2.0);

fmt::print("hello World");

fmt::print("{}","wie geht es dir mir geht es gut");
}

void test(double &x,double &index) {
while (0.0 > 10.0) {
fmt::print("{}",i);

i = i + 1;;
}
}
int main() {Start();}
