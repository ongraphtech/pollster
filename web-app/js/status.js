angular.module('grailsService', ['ngResource']).factory('Grails', function ($resource) {
    var baseUrl = $('body').data('base-url');
    return $resource(baseUrl + ':action/:id', {id:'@id'}, {
        list:{method:'GET', params:{action:'list'}, isArray:true},
        display:{method:'GET', params:{action:'display'}},
        get:{method:'GET', params:{action:'fetch'}},
        save:{method:'POST', params:{action:'save'}},
        update:{method:'POST', params:{action:'update'}},
        delete:{method:'POST', params:{action:'delete'}},
        board:{method:'GET', params:{action:'board'}, isArray:true}
    });
});

var statusModule = angular.module('statusScript', ['grailsService', 'flashService', 'charts.pie', 'ngCookies', 'wordLimiter', 'pagination']);

statusModule.run(['$rootScope', '$cookieStore', '$location', '$http', function ($rootScope, $cookieStore, $location, $http) {
    $rootScope.$on('$routeChangeStart', function () {
        var isLogin = $cookieStore.get('name');
        var userId = $cookieStore.get('userId');
        if (!isLogin) {
            $location.path("/login");
        } else {
            $http.get('/pollster/status/autoLoginByCookie?id=' + userId).success(function (list) {

            });
        }
    });

    $rootScope.logout = function () {
        var url = "/pollster/status/logout";
        $http.get(url).success(function () {
            $rootScope.displayName = '';
            $cookieStore.remove('name');
            $cookieStore.remove('userId');
            $location.path("/login");
        });
    }
}]);

statusModule.config(['$routeProvider', function ($routeProvider) {
    var baseUrl = $('body').data('template-url');
    $routeProvider.
        when('/create', {templateUrl:baseUrl + '/create.html', controller:CreateCtrl}).
        when('/edit/:id', {templateUrl:baseUrl + '/edit.html', controller:EditCtrl}).
        when('/list', {templateUrl:baseUrl + '/list.html', controller:ListCtrl}).
        when('/board', {templateUrl:baseUrl + '/board.html', controller:BoardCtrl}).
        when('/show/:id/:name', {templateUrl:baseUrl + '/show.html', controller:ShowCtrl}).
        when('/display/:id', {templateUrl:baseUrl + '/display.html', controller:displayCtrl}).
        when('/login', {templateUrl:baseUrl + '/login.html', controller:loginCtrl}).
        otherwise({redirectTo:'/login'});
}]);

Function.prototype.curry = function () {
    if (arguments.length < 1) {
        return this;
    }
    var __method = this;
    var args = toArray(arguments);
    return function () {
        return __method.apply(this, args.concat(toArray(arguments)));
    }
}

function ListCtrl($scope, $routeParams, $location, Grails, Flash, $timeout) {
    Grails.list($routeParams, function (list, headers) {
        $scope.list = list;
        $scope.total = parseInt(headers('X-Pagination-Total'));
        $scope.message = Flash.getMessage();
    }, errorHandler.curry($scope, $location, Flash));

    $scope.show = function (item) {
        $location.path('/show/' + item.id + '/shashank');
    };

    $scope.edit = function (item) {
        $location.path('/edit/' + item.id);
    };

    $scope.delete = function (item) {
        item.$delete(function (response) {
            window.location.reload();
        }, errorHandler.curry($scope, $location, Flash));
    };

    $scope.$on('$destroy', function () {
        $timeout.cancel(timeout);
    });
}

function BoardCtrl($scope, $http, $location, $timeout) {
    getList();
    setTimer();
    function getList() {
        $http.get('/pollster/status/board').success(function (list) {
            $scope.list = list;
        });
    }

    function setTimer() {
        timeout = $timeout(function () {
            getList();
            setTimer();
        }, 1000);
    }

    $scope.vote = function (val, id) {
        $http.get('/pollster/status/rateStatus?statusVote=' + val + '&id=' + id + '').success(function (data) {
        });
    };

    $scope.display = function (item) {
        $location.path('/display/' + item.id);
    };

    $scope.$on('$destroy', function () {
        $timeout.cancel(timeout);
    });
}

function loginCtrl($scope, $http, $cookieStore, $location, $rootScope) {
    var isLogin = $cookieStore.get('userId');
    if (isLogin) {
        var name = $cookieStore.get('name');
        $rootScope.displayName = name;
        $location.path("/board");
    }
    $rootScope.performLogin = function () {
        var url = "/pollster/status/login" + "?username=" + $scope.username + "&password=" + $scope.password;
        $http.get(url).success(function (data) {
            if (data.isUserExist) {
                $rootScope.displayName = data.user.name;
                $location.path("/board");
                $cookieStore.put('userId', data.user.id);
                $cookieStore.put('name', data.user.name);
            }
            else {
                $location.path("/login")
            }
        });
    };
}

function displayCtrl($scope, $routeParams, $location, Grails, Flash, $http, $timeout) {
    $scope.message = Flash.getMessage();
    getData();
    setTimer();
    function getData() {
        Grails.display({id:$routeParams.id}, function (item) {
            $scope.item = item;
            $scope.data = [
                ['Like', item.upVote],
                ['Dislike', item.downVote]
            ];
        }, errorHandler.curry($scope, $location, Flash));
    }

    function setTimer() {
        timeout = $timeout(function () {
            getData();
            setTimer();
        }, 500);
    }

    $scope.invite = function (item) {
        $http.get('/pollster/status/inviteUser?email=' + $scope.inviteemail + '&id=' + $scope.item.id + '').success(function (data) {
            $scope.inviteemail = '';
        });
    };

    $scope.vote = function (val) {
        $http.get('/pollster/status/rateStatus?statusVote=' + val + '&id=' + $scope.item.id + '').success(function (data) {
            $scope.inviteemail = '';
        });
    };

    $scope.$on('$destroy', function () {
        $timeout.cancel(timeout);
    });
}

function ShowCtrl($scope, $routeParams, $location, Grails, Flash) {
    $scope.message = Flash.getMessage();

    Grails.get({id:$routeParams.id, name:$routeParams.name}, function (item) {
        $scope.item = item;
    }, errorHandler.curry($scope, $location, Flash));

    $scope.delete = function (item) {
        item.$delete(function (response) {
            Flash.success(response.message);
            $location.path('/list');
        }, errorHandler.curry($scope, $location, Flash));
    };
}

function CreateCtrl($scope, $location, Grails, Flash) {
    $scope.item = new Grails;
    $scope.save = function (item) {
        item.$save(function (response) {
            Flash.success(response.message);
            $location.path('/show/' + response.id);
        }, errorHandler.curry($scope, $location, Flash));
    };
}

function EditCtrl($scope, $routeParams, $location, Grails, Flash) {
    Grails.get({id:$routeParams.id}, function (item) {
        $scope.item = item;
    }, errorHandler.curry($scope, $location, Flash));

    $scope.update = function (item) {
        item.$update(function (response) {
            Flash.success(response.message);
            $location.path('/show/' + response.id);
        }, errorHandler.curry($scope, $location, Flash));
    };

    $scope.delete = function (item) {
        item.$delete(function (response) {
            Flash.success(response.message);
            $location.path('/list');
        }, errorHandler.curry($scope, $location, Flash));
    };
}

angular.module('charts.pie', []).directive('qnPiechart', [function () {
    return {
        require:'?ngModel',
        link:function (scope, element, attr, controller) {
            var settings = {
                is3D:true
            };

            var getOptions = function () {
                return angular.extend({ }, settings, scope.$eval(attr.qnPiechart));
            };

            // creates instance of datatable and adds columns from settings
            var getDataTable = function () {
                var columns = scope.$eval(attr.qnColumns);
                var data = new google.visualization.DataTable();
                angular.forEach(columns, function (column) {
                    data.addColumn(column.type, column.name);
                });
                return data;
            };

            var init = function () {
                var options = getOptions();
                if (controller) {
                    var drawChart = function () {
                        var data = getDataTable();
                        // set model
                        var rowVal = controller.$viewValue ? controller.$viewValue : [];
                        data.addRows(rowVal);
                        // Instantiate and draw our chart, passing in some options.
                        var pie = new google.visualization.PieChart(element[0]);
                        pie.draw(data, options);
                    };

                    controller.$render = function () {
                        drawChart();
                    };
                }

                if (controller) {
                    // Force a render to override
                    controller.$render();
                }
            };

            // Watch for changes to the directives options
            scope.$watch(getOptions, init, true);
            scope.$watch(getDataTable, init, true);
        }
    };
}]);

angular.module('wordLimiter', []).filter('cut', function () {
    return function (value, wordwise, max, tail) {
        if (!value) return '';

        max = parseInt(max, 10);
        if (!max) return value;
        if (value.length <= max) return value;

        value = value.substr(0, max);
        if (wordwise) {
            var lastspace = value.lastIndexOf(' ');
            if (lastspace != -1) {
                value = value.substr(0, lastspace);
            }
        }
        return value + (tail || ' …');
    };
});