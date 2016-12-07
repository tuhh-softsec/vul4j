/*
 * Copyright (c) 2014 T-Systems Multimedia Solutions GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var $ = require('bootstrap-detached').getBootstrap();
require('./prototypecompat.js');
require('./tabhashes.js');
var wurl = require('wurl');
require('datatables.net-bs')(window, $);
require('lightbox2');
require('./jquery.gridster')(window, $);

$(document).ready(function () {
    "use strict";

    var grid = [];
    $.fn.dataTableExt.sErrMode = 'none';

    $('.panel-body').each(function () {
        if (!/[\S]/.test($(this).html())) {
            $(this).html('no PDF reports available!');
        }
    });

    $(".tab-pane").each(function (pageIndex, page) {
        try {
            $(".table", this).has("tbody").dataTable({
                "stateSave": false,
                "order": [0, 'desc']
            });
        } catch (e) {
            console.log(e);
        }

        $("#measureGroup", this).change(function () {
            if ($(this).val() === 'UnitTest overview') {
                $("#measure", page).parent().hide();
                $("#aggregation", page).parent().hide();
                $("#customName", page).val('');
            } else {
                projectAction.getAvailableMeasures($(page).attr('id'), $(this).val(), function (data) {
                    $("#measure", page).empty();
                    $("#customName", page).val("");
                    $("#customBuildCount", page).val("");
                    $("#measure", page).parent().show();
                    $("#aggregation", page).parent().show();
                    $.each(data.responseObject(), function (val, text) {
                        $("#measure", page).append($('<option></option>').val(val).html(text));
                    });
                    $("#measure", page).trigger("change");
                });
            }
        });

        $("#measure", this).change(function () {
            projectAction.getAggregationFromMeasure($(page).attr('id'), $("#measureGroup", page).children(":selected").text(),
                $(this).children(":selected").text(), function (data) {
                    $("#aggregation", page).val(data.responseObject());
                    $("#aggregation", page).trigger("change");
                });
        });

        $("#aggregation", this).change(function () {
            $("#customName", page).val(generateTitle($("#measure").children(":selected").text(), $("#measureGroup", page).children(":selected").text(),
                $(this).children(":selected").text()));
        });

        $("#editbutton", this).click(function () {
            $(this).hide();
            $(".img-thumbnail", page).unwrap();
            $("#measureGroup", page).trigger("change");
            $("#donebutton", page).show();
            $("#cancelbutton", page).show();
            $("#editform", page).show();
            $(".del_img", page).show();
            $(".chk_show", page).show();
            grid[pageIndex].enable();
        });
        $("#cancelbutton", this).click(function () {
            location.reload(true);
        });

        $("#addbutton", this).click(function () {
            var request_parameter = '&amp;width=410&amp;height=300&amp;customName=' + encode($("#customName", page).val()) +
                '&amp;customBuildCount=' + $("#customBuildCount", page).val() + '&amp;aggregation=' + $("#aggregation", page).val();
            if ($("#measureGroup", page).val() === 'UnitTest overview') {
                grid[pageIndex].add_widget('<li><img class="img-thumbnail" height="300" width="410" ' +
                    'src="./testRunGraph?id=unittest_overview' + request_parameter + '">' +
                    '<span class="del_img glyphicon glyphicon-remove"></span>' +
                    '<span class="chk_show"><input type="checkbox" title="show in project overview" checked="checked"/></span></li>', 1, 1);
            } else {
                grid[pageIndex].add_widget('<li><img class="img-thumbnail" height="300" width="410" ' +
                    'src="./summarizerGraph?id=' + $("#measure", page).val() + request_parameter + '">' +
                    '<span class="del_img glyphicon glyphicon-remove"></span>' +
                    '<span class="chk_show"><input type="checkbox" title="show in project overview" checked="checked"/></span></li>', 1, 1);
            }
            $(".del_img", page).click(function () {
                grid[pageIndex].remove_widget($(this).parent());
            });
        });
        $("#donebutton", this).click(function () {
            projectAction.setDashboardConfiguration($(page).attr('id'), JSON.stringify(grid[pageIndex].serialize()), function () {
                location.reload(true);
            });
        });

        $('#tabList').find('a').eq(pageIndex).tab('show'); // very messy :(
        if ($(".gridster ul", page).length != 0) {
            grid[pageIndex] = $(".gridster ul", page).gridster({
                namespace: "#" + $(page).attr('id'),
                widget_base_dimensions: [364, 267],
                widget_margins: [5, 5],

                serialize_params: function ($w, wgd) {
                    return {
                        col: wgd.col,
                        row: wgd.row,
                        id: getURLParameter($("img", $w), "id"),
                        dashboard: $(page).attr('id'),
                        chartDashlet: getURLParameter($("img", $w), "chartDashlet"),
                        measure: getURLParameter($("img", $w), "measure"),
                        customName: getURLParameter($("img", $w), "customName"),
                        customBuildCount: getURLParameter($("img", $w), "customBuildCount"),
                        show: $("input[type='checkbox']", $w).prop('checked'),
                        aggregation: getURLParameter($("img", $w), "aggregation")
                    };
                }
            }).data('gridster').disable();

            projectAction.getDashboardConfiguration($(page).attr('id'), function (data) {
                var json = JSON.parse(data.responseObject());
                $.each(json, function (index) {
                    if (json[index].dashboard == $(page).attr('id')) {
                        if (json[index].id === 'unittest_overview') {
                            grid[pageIndex].add_widget('<li><a href="./testRunGraph?width=800&amp;height=585&amp;id=unittest_overview" ' +
                                'data-lightbox="' + $(page).attr('id') + '"><img class="img-thumbnail" height="300" width="410" ' +
                                'src="./testRunGraph?width=410&amp;height=300&amp;id=unittest_overview"></a>' +
                                '<span class="del_img glyphicon glyphicon-remove"></span>' +
                                '<span class="chk_show"><input type="checkbox" title="show in project overview" checked="checked"/></span></li>', 1, 1,
                                json[index].col, json[index].row);
                        } else {
                            grid[pageIndex].add_widget('<li><a href="./summarizerGraph?width=800&amp;height=585&amp;id=' + json[index].id + '" ' +
                                'data-lightbox="' + $(page).attr('id') + '" data-title="' + json[index].description + '">' +
                                '<img class="img-thumbnail" height="300" width="410" src="./summarizerGraph?width=410&amp;height=300&amp;id=' + json[index].id + '' +
                                '" title="source: ' + json[index].chartDashlet + '-' + json[index].measure + ' (' + json[index].aggregation + ')\n' + json[index].description + '"></a>' +
                                '<span class="del_img glyphicon glyphicon-remove"></span><span class="chk_show">' +
                                '<input type="checkbox" title="show in project overview" ' + (json[index].show ? "checked='checked'" : "") +
                                '/></span></li>', 1, 1, json[index].col, json[index].row);
                        }
                    }
                });
                $(".chk_show", page).hide();
                $(".del_img", page).hide().click(function () {
                    grid[pageIndex].remove_widget($(this).parent());
                });
            });
        }
    });

    var hash = window.location.hash;
    if (hash) {
        $('ul.nav a[href="' + hash + '"]').tab('show');
    } else {
        $('#tabList').find('a:first').tab('show'); // Select first tab
    }

    $('.nav-tabs a').click(function () {
        $(this).tab('show');
        var scrollmem = $('body').scrollTop() || $('html').scrollTop();
        window.location.hash = this.hash;
        $('html,body').scrollTop(scrollmem);
    });
});

function generateTitle(measure, chartDashlet, aggregation) {
    var chartDashletName = measure.replace(/\s/g, '') === chartDashlet.replace(/\s/g, '') ? chartDashlet : chartDashlet + ' - ' + measure;
    return chartDashletName + ' (' + aggregation + ')';
}

function getURLParameter(obj, parameter) {
    return $(obj).attr("src").indexOf(parameter) > -1 ? wurl("?" + parameter, $(obj).attr("src")) : ""
}

function encode(toEncode) {
    return encodeURIComponent(toEncode)
        .replace(/!/g, '%21')
        .replace(/'/g, '%27')
        .replace(/\(/g, '%28')
        .replace(/\)/g, '%29')
        .replace(/\*/g, '%2A');
}
