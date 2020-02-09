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

template <typename T>
T __internal__array_access(const std::vector<T> buffer) {
  
}
struct Person {
    std::string name;
    double alter;
};
	void to_json(json& j, const Person& __param__Person) {
	j = json{{"name", __param__Person.name},
{"alter", __param__Person.alter},
};
}

	template <>
	struct fmt::formatter<Person> {
	constexpr auto parse(format_parse_context& ctx) { return ctx.begin(); }

	template <typename FormatContext>
	auto format(const Person& __param__Person, FormatContext& ctx) {
		json j = __param__Person;
		return format_to(ctx.out(), "{}", j.dump(4));
	}
};


void start() {
auto me = Person {.name = "Simon", .alter = 18.0,};

if (me.name == "Simon") {
fmt::print("Hallo Simon");
}

auto bar = foo();

auto bar = 20.0;

auto bar = "test";

fmt::print("{}\n",me);
}

double addiere(double &x,double &y) {
return x + y;
}
int main() {
	Start();
}
