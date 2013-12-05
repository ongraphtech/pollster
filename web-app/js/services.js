angular.module('flashService', []).factory('Flash', function () {
    var flash = {};
    flash.getMessage = function () {
        var value = this.message;
        this.message = undefined;
        return value;
    };
    flash.error = function (text) {
        this.message = {level:'error', text:text};
    };
    flash.success = function (text) {
        this.message = {level:'success', text:text};
    };
    flash.info = function (text) {
        this.message = {level:'info', text:text};
    };
    return flash;
});

function errorHandler($scope, $location, Flash, response) {
    switch (response.status) {
        case 404:
            Flash.error(response.data.message);
            $location.path('/list');
            break;
        case 409:
            $scope.message = {level:'error', text:response.data.message};
            break;
        case 422:
            $scope.errors = response.data.errors;
            break;
        default: // TODO: general error handling
    }
}

function toArray(element) {
    return Array.prototype.slice.call(element);
}

angular.module('pagination', []).directive('pagination', [function () {
    var baseUrl = $('body').data('common-template-url');
    return {
        restrict:'A',
        transclude:false,
        scope:{
            total:'=total'
        },
        controller:function ($scope, $routeParams) {
            $scope.max = parseInt($routeParams.max) || 10;
            $scope.offset = parseInt($routeParams.offset) || 0;
            $scope.currentPage = Math.ceil($scope.offset / $scope.max);
            $scope.pages = function () {
                var pages = [];
                for (var i = 0; i < Math.ceil($scope.total / $scope.max); i++)
                    pages.push(i);
                return pages;
            };
            $scope.lastPage = function () {
                return $scope.pages().slice(-1)[0];
            };
        },
        templateUrl:baseUrl + '/pagination.html',
        replace:false
    }
}]);