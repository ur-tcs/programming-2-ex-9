package scalashop

class TimeSpentSuite extends munit.FunSuite:
  test("After completing the lab, please report how long you spent on it"):
    assert(howManyHoursISpentOnThisLab() > 0.0)
