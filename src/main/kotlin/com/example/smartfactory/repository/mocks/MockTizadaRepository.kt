package com.example.smartfactory.repository.mocks

import com.example.smartfactory.Domain.Tizada.Tizada
import com.example.smartfactory.Domain.Tizada.TizadaConfiguration
import com.example.smartfactory.Domain.Tizada.TizadaPart
import com.example.smartfactory.Domain.Tizada.TizadaState
import com.example.smartfactory.repository.TizadaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*


@Repository
class MockTizadaRepository: TizadaRepository {
    val parts = mutableListOf(TizadaPart(UUID.randomUUID(), "<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' version='1.1' id='svg2' x='0px' y='0px' width='1147.592px' height='1397.27px' viewBox='0 0 1147.592 1397.27' enable-background='new 0 0 1147.592 1397.27' xml:space='preserve'><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='684.045,443.734 688.396,447.215   666.488,450.935 666.488,432.651 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='697.067,404.901 697.067,415.601   709.719,415.905 710.293,406.067 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='723.908,440.886 715.362,442.463   709.719,435.85 712.627,427.66 721.17,426.079 726.81,432.692 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='617.292,458.369 618.742,465.049   599.735,465.566 599.735,447.28 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='619.963,428.025 619.963,438.722   633.189,438.467 633.189,429.192 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='646.805,464.01 638.259,465.587   632.618,458.974 635.523,450.784 644.069,449.207 649.706,455.819 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='777.062,428.025 746.983,457.397   760.468,481.256 782.898,488.173 788.995,478.145 814.669,479.959 818.817,438.722 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='666.589,476.434 664.255,503.557   681.632,505.633 694.857,499.916 692.262,483.07 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='599.735,501.612 636.247,531.953   680.981,531.953 680.981,554.904 599.735,554.904 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='759.315,529.301 775.721,500.886   792.128,529.301 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='741.307,428.338 734.229,419.461   736.757,408.396 746.983,403.469 757.206,408.396 759.735,419.461 752.654,428.338 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='647.399,599.509 651.749,602.988   629.84,606.702 629.84,588.419 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='650.068,569.167 650.068,579.861   662.723,580.171 663.294,570.334 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='758.061,626.35 800.104,626.854   807.438,600.447 796.468,579.694 785.065,582.478 768.195,563.04 736.104,589.264 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='694.308,592.604 687.232,583.727   689.758,572.661 699.984,567.735 710.211,572.661 712.736,583.727 705.658,592.604 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='854.273,544.551 858.625,548.03   836.717,551.75 836.717,533.467 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='856.944,514.209 856.944,524.909   869.597,525.213 870.171,515.376 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='883.785,550.194 875.24,551.771   869.597,545.158 872.505,536.968 881.048,535.388 886.688,542.001 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='779.841,537.333 779.841,548.03   793.067,547.775 793.067,538.5 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='901.184,537.646 894.106,528.769   896.635,517.703 906.861,512.777 917.084,517.703 919.613,528.769 912.532,537.646 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='836.717,410.401 827.633,456.421   894.106,463.102 910.101,434.273 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='676.909,605.146 668.363,606.727   662.723,600.113 665.628,591.92 674.174,590.346 679.814,596.959 '></polygon><rect x='21.066' y='439.913' width='30.913' height='18.155' fill='none' stroke='#010101'></rect><rect x='106.758' y='452.881' width='29.563' height='5.188' fill='none' stroke='#010101'></rect><rect x='184.038' y='464.809' width='25.963' height='17.637' fill='none' stroke='#010101'></rect><rect x='305.408' y='427.01' width='17.01' height='40.393' fill='none' stroke='#010101'></rect><rect x='262.876' y='458.068' width='17.004' height='17.633' fill='none' stroke='#010101'></rect><rect x='338.931' y='427.01' width='47.904' height='25.868' fill='none' stroke='#010101'></rect><polygon fill='none' stroke='#010101' points='66.82,475.701 47.112,507.86 101.95,510.972 140.64,493.336 90.161,496.448 87.322,473.625 '></polygon><polygon fill='none' stroke='#010101' points='196.401,495.929 224.016,523.938 348.972,517.196 271.061,492.297 227.967,473.625 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='1065.045,441.528 1069.396,445.009   1047.488,448.729 1047.488,430.445 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='1078.067,402.694 1078.067,413.395   1090.719,413.699 1091.293,403.861 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='1104.908,438.68 1096.362,440.257   1090.719,433.644 1093.627,425.454 1102.17,423.873 1107.811,430.486 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='998.292,456.163 999.742,462.843   980.735,463.359 980.735,445.074 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='1000.963,425.818 1000.963,436.516   1014.189,436.261 1014.189,426.985 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='1027.805,461.804 1019.259,463.381   1013.618,456.768 1016.523,448.578 1025.069,447.001 1030.706,453.613 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='601.531,174.639 571.451,204.011   584.936,227.87 607.366,234.787 613.463,224.759 639.137,226.573 643.285,185.336 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='1047.589,474.228 1045.255,501.351   1062.632,503.427 1075.857,497.71 1073.262,480.864 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='980.735,499.406 1017.247,529.747   1061.98,529.747 1061.98,552.698 980.735,552.698 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='583.783,275.915 600.188,247.5   616.596,275.915 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='565.774,174.952 558.696,166.075   561.225,155.01 571.451,150.083 581.674,155.01 584.203,166.075 577.122,174.952 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='1028.399,597.303 1032.749,600.782   1010.84,604.496 1010.84,586.213 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='1031.068,566.961 1031.068,577.655   1043.723,577.965 1044.294,568.128 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='582.528,372.964 624.57,373.468   631.906,347.062 620.936,326.309 609.533,329.092 592.663,309.654 560.571,335.878 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='1075.308,590.397 1068.232,581.521   1070.758,570.455 1080.984,565.529 1091.211,570.455 1093.736,581.521 1086.658,590.397 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='678.741,291.165 683.093,294.645   661.185,298.364 661.185,280.081 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='681.412,260.823 681.412,271.524   694.064,271.827 694.639,261.99 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='708.253,296.809 699.708,298.386   694.064,291.773 696.973,283.582 705.516,282.002 711.155,288.615 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='604.309,283.947 604.309,294.645   617.535,294.389 617.535,285.114 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='725.651,284.26 718.574,275.383   721.104,264.317 731.329,259.392 741.552,264.317 744.081,275.383 737,284.26 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='661.185,157.016 652.101,203.035   718.574,209.716 734.568,180.887 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='1057.909,602.939 1049.363,604.521   1043.723,597.907 1046.628,589.714 1055.174,588.14 1060.814,594.753 '></polygon><rect x='402.066' y='437.707' width='30.912' height='18.155' fill='none' stroke='#010101'></rect><rect x='487.758' y='450.675' width='29.564' height='5.188' fill='none' stroke='#010101'></rect><rect x='565.949' y='691.507' width='25.963' height='17.636' fill='none' stroke='#010101'></rect><rect x='687.32' y='653.708' width='17.01' height='40.392' fill='none' stroke='#010101'></rect><rect x='644.789' y='684.767' width='17.004' height='17.634' fill='none' stroke='#010101'></rect><rect x='720.844' y='653.708' width='47.904' height='25.868' fill='none' stroke='#010101'></rect><polygon fill='none' stroke='#010101' points='447.82,473.495 428.112,505.654 482.95,508.766 521.641,491.13 471.161,494.242 468.322,471.419 '></polygon><polygon fill='none' stroke='#010101' points='578.312,722.627 605.928,750.635 730.885,743.895 652.973,718.995 609.879,700.323 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='903.513,195.164 907.864,198.645   885.956,202.364 885.956,184.081 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='916.535,156.33 916.535,167.03   929.188,167.335 929.761,157.497 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='943.376,192.316 934.83,193.893   929.188,187.279 932.095,179.09 940.638,177.509 946.277,184.122 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='836.76,209.799 838.209,216.479   819.203,216.995 819.203,198.71 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='839.432,179.454 839.432,190.151   852.657,189.897 852.657,180.621 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='866.271,215.44 857.727,217.017   852.086,210.403 854.991,202.214 863.537,200.637 869.174,207.249 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='996.531,179.454 966.451,208.826   979.936,232.686 1002.366,239.603 1008.463,229.574 1034.137,231.389 1038.285,190.151 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='886.057,227.863 883.723,254.986   901.1,257.063 914.325,251.346 911.729,234.5 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='819.203,253.042 855.715,283.383   900.449,283.383 900.449,306.334 819.203,306.334 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='978.783,280.731 995.188,252.316   1011.596,280.731 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='960.774,179.768 953.696,170.891   956.225,159.825 966.451,154.899 976.674,159.825 979.203,170.891 972.122,179.768 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='866.867,350.939 871.217,354.418   849.308,358.132 849.308,339.849 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='869.536,320.597 869.536,331.291   882.19,331.601 882.762,321.764 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='977.528,377.779 1019.57,378.283   1026.906,351.877 1015.936,331.124 1004.533,333.907 987.663,314.47 955.571,340.693 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='913.775,344.033 906.7,335.156   909.226,324.091 919.452,319.165 929.679,324.091 932.204,335.156 925.126,344.033 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='1073.741,295.981 1078.093,299.46   1056.186,303.18 1056.186,284.897 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='1076.412,265.639 1076.412,276.339   1089.064,276.643 1089.639,266.806 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='1103.253,301.624 1094.708,303.201   1089.064,296.588 1091.973,288.398 1100.516,286.817 1106.155,293.431 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='999.309,288.763 999.309,299.46   1012.535,299.204 1012.535,289.93 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='1120.651,289.075 1113.574,280.198   1116.104,269.133 1126.329,264.207 1136.552,269.133 1139.081,280.198 1132,289.075 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='1056.186,161.831 1047.101,207.851   1113.574,214.531 1129.568,185.702 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='896.377,356.575 887.831,358.156   882.19,351.543 885.096,343.35 893.642,341.776 899.282,348.389 '></polygon><rect x='797.979' y='671.427' width='30.912' height='18.155' fill='none' stroke='#010101'></rect><rect x='883.67' y='684.395' width='29.564' height='5.188' fill='none' stroke='#010101'></rect><rect x='916.535' y='655.978' width='25.963' height='17.637' fill='none' stroke='#010101'></rect><rect x='1037.906' y='618.179' width='17.01' height='40.393' fill='none' stroke='#010101'></rect><rect x='995.375' y='649.237' width='17.004' height='17.633' fill='none' stroke='#010101'></rect><rect x='1071.43' y='618.179' width='47.904' height='25.868' fill='none' stroke='#010101'></rect><polygon fill='none' stroke='#010101' points='843.732,707.215 824.023,739.374 878.861,742.484 917.553,724.85 867.072,727.962 864.234,705.139 '></polygon><polygon fill='none' stroke='#010101' points='928.898,687.098 956.514,715.105 1081.471,708.365 1003.559,683.466 960.465,664.794 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='105.375,574.337 109.727,577.817   87.819,581.537 87.819,563.254 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='118.398,535.503 118.398,546.203   131.049,546.508 131.624,536.67 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='145.239,571.488 136.693,573.065   131.049,566.452 133.958,558.263 142.5,556.682 148.14,563.295 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='38.623,588.972 40.072,595.651   21.066,596.168 21.066,577.883 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='41.294,558.627 41.294,569.324   54.52,569.069 54.52,559.794 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='68.135,594.612 59.589,596.189   53.949,589.576 56.854,581.387 65.4,579.81 71.037,586.422 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='198.394,558.627 168.314,587.999   181.798,611.858 204.229,618.775 210.326,608.747 236,610.562 240.148,569.324 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='87.919,607.036 85.585,634.159   102.962,636.235 116.188,630.519 113.592,613.673 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='21.066,632.215 57.578,662.556   102.312,662.556 102.312,685.507 21.066,685.507 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='180.646,659.903 197.051,631.488   213.458,659.903 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='162.637,558.94 155.559,550.062   158.087,538.998 168.314,534.071 178.537,538.998 181.066,550.062 173.985,558.94 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='68.73,730.111 73.08,733.591   51.17,737.305 51.17,719.021 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='71.399,699.77 71.399,710.464   84.053,710.773 84.625,700.938 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='115.638,723.206 108.563,714.329   111.088,703.264 121.315,698.338 131.542,703.264 134.067,714.329 126.989,723.206 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='275.604,675.153 279.956,678.633   258.047,682.354 258.047,664.069 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='278.275,644.812 278.275,655.512   290.927,655.815 291.501,645.979 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='305.116,680.797 296.571,682.374   290.927,675.761 293.835,667.57 302.378,665.99 308.018,672.604 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='201.171,667.936 201.171,678.633   214.398,678.377 214.398,669.104 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='322.514,668.248 315.438,659.371   317.965,648.306 328.192,643.38 338.415,648.306 340.944,659.371 333.863,668.248 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='258.047,541.004 248.963,587.023   315.438,593.704 331.431,564.875 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='98.24,735.748 89.694,737.329   84.053,730.716 86.958,722.521 95.504,720.948 101.145,727.562 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='504.59,600.795 508.941,604.275   487.033,607.995 487.033,589.712 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='517.611,561.961 517.611,572.661   530.264,572.966 530.838,563.128 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='545.365,716.138 536.818,717.715   531.176,711.102 534.084,702.912 542.627,701.331 548.266,707.944 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='437.837,615.43 439.287,622.109   420.28,622.626 420.28,604.341 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='440.508,585.085 440.508,595.782   453.734,595.527 453.734,586.252 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='467.35,621.07 458.804,622.646   453.163,616.034 456.068,607.845 464.614,606.268 470.251,612.88 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='598.52,813.989 568.439,843.361   581.924,867.221 604.355,874.138 610.451,864.109 636.125,865.924 640.273,824.687 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='487.134,633.494 484.8,660.617   502.177,662.693 515.402,656.977 512.807,640.131 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='420.28,658.673 456.792,689.014   501.526,689.014 501.526,711.965 420.28,711.965 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='580.771,915.266 597.178,886.851   613.584,915.266 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='562.764,814.303 555.686,805.426   558.213,794.359 568.439,789.434 578.662,794.359 581.191,805.426 574.111,814.303 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='467.944,756.569 472.294,760.049   450.385,763.763 450.385,745.479 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='470.613,726.229 470.613,736.922   483.268,737.231 483.839,727.395 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='514.852,749.664 507.777,740.787   510.303,729.722 520.529,724.796 530.756,729.722 533.281,740.787 526.203,749.664 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='675.73,930.516 680.082,933.995   658.174,937.715 658.174,919.432 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='678.4,900.174 678.4,910.874   691.053,911.178 691.627,901.341 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='705.242,936.159 696.697,937.736   691.053,931.123 693.961,922.933 702.504,921.353 708.145,927.966 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='601.297,923.298 601.297,933.995   614.523,933.739 614.523,924.465 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='563.383,919.432 559.621,926.865   550.156,929.546 550.156,937.715 559.621,940.869 563.383,946.966 571.941,946.966 575.18,942.553 581.07,937.736 581.07,929.546   575.18,926.087 570.123,919.432 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='722.641,923.609 715.562,914.734   718.092,903.668 728.318,898.742 738.541,903.668 741.07,914.734 733.988,923.609 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='658.174,796.366 649.09,842.386   715.562,849.066 731.557,820.237 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='497.454,762.206 488.908,763.787   483.268,757.174 486.173,748.98 494.719,747.406 500.359,754.02 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='900.502,834.515 904.854,837.995   882.945,841.715 882.945,823.432 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='913.523,795.681 913.523,806.381   926.176,806.686 926.75,796.848 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='940.365,831.666 931.818,833.243   926.176,826.63 929.084,818.441 937.627,816.859 943.266,823.473 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='833.748,849.15 835.197,855.829   816.191,856.346 816.191,838.061 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='836.42,818.805 836.42,829.502   849.646,829.247 849.646,819.972 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='863.262,854.79 854.715,856.367   849.074,849.754 851.98,841.564 860.525,839.987 866.162,846.6 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='993.52,818.805 963.439,848.177   976.924,872.036 999.355,878.953 1005.451,868.925 1031.125,870.739 1035.273,829.502 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='957.764,819.118 950.686,810.241   953.213,799.176 963.439,794.249 973.662,799.176 976.191,810.241 969.111,819.118 '></polygon><polygon fill='none' stroke='#010101' stroke-miterlimit='10' points='1053.174,801.182 1044.09,847.201   1110.562,853.882 1126.557,825.053 '></polygon><path fill='none' stroke='#010101' d='M746.843,60.679c-2.465,1.232-7.395,2.465-13.711,2.465c-14.635,0-25.65-9.243-25.65-26.266   c0-16.252,11.016-27.268,27.113-27.268c6.471,0,10.553,1.387,12.324,2.311l-1.617,5.469c-2.542-1.232-6.162-2.157-10.476-2.157   c-12.17,0-20.258,7.78-20.258,21.414c0,12.709,7.317,20.874,19.95,20.874c4.082,0,8.241-0.848,10.938-2.157L746.843,60.679z'></path><path fill='none' stroke='#010101' d='M755.008,7.685h6.778v54.688h-6.778V7.685z'></path><path fill='none' stroke='#010101' d='M 780.734 14.617 c 0.07700000000000001 2.311 -1.617 4.16 -4.313 4.16 c -2.388 0 -4.082 -1.8490000000000002 -4.082 -4.16 c 0 -2.388 1.771 -4.236 4.236 -4.236 C 779.117 10.38 780.734 12.229 780.734 14.617 z'></path><path fill='none' stroke='#010101' d='M 773.187 62.373 V 25.092 h 6.777 v 37.281 H 773.187 z'></path><path fill='none' stroke='#010101' d='M817.784,60.986c-1.771,0.925-5.7,2.157-10.707,2.157c-11.246,0-18.563-7.626-18.563-19.026   c0-11.477,7.856-19.795,20.027-19.795c4.005,0,7.548,1.001,9.397,1.925l-1.541,5.238c-1.617-0.924-4.159-1.771-7.856-1.771   c-8.55,0-13.172,6.316-13.172,14.096c0,8.627,5.546,13.942,12.94,13.942c3.852,0,6.394-1.001,8.318-1.849L817.784,60.986z'></path><path fill='none' stroke='#010101' d='M832.42,42.192h0.154c0.924-1.31,2.233-2.927,3.312-4.237l10.938-12.863h8.164L840.585,40.42l16.406,21.953h-8.241   l-12.864-17.87l-3.466,3.852v14.019h-6.701V7.685h6.701V42.192z'></path><path fill='none' stroke='#010101' d='M885.107,16.157h-15.79v-5.7h38.437v5.7h-15.867v46.216h-6.779V16.157z'></path><path fill='none' stroke='#010101' d='M912.762,7.685h6.778v23.262h0.154c1.078-1.926,2.772-3.62,4.853-4.775c2.002-1.156,4.391-1.926,6.932-1.926   c5.007,0,13.018,3.081,13.018,15.944v22.184h-6.778V40.96c0-6.008-2.233-11.092-8.627-11.092c-4.39,0-7.856,3.081-9.089,6.778   c-0.385,0.924-0.462,1.925-0.462,3.235v22.492h-6.778V7.685z'></path><path fill='none' stroke='#010101' d='M 963.062 14.617 c 0.07700000000000001 2.311 -1.6179999999999999 4.16 -4.313 4.16 c -2.3890000000000002 0 -4.083 -1.8490000000000002 -4.083 -4.16 c 0 -2.388 1.771 -4.236 4.236 -4.236 C 961.443 10.38 963.062 12.229 963.062 14.617 z'></path><path fill='none' stroke='#010101' d='M 955.513 62.373 V 25.092 h 6.7780000000000005 v 37.281 H 955.513 z'></path><path fill='none' stroke='#010101' d='M972.611,55.44c2.003,1.31,5.546,2.696,8.936,2.696c4.93,0,7.24-2.465,7.24-5.546c0-3.235-1.926-5.007-6.933-6.855   c-6.701-2.388-9.859-6.085-9.859-10.553c0-6.008,4.853-10.938,12.864-10.938c3.773,0,7.086,1.078,9.166,2.311l-1.695,4.93   c-1.463-0.924-4.159-2.157-7.625-2.157c-4.006,0-6.239,2.311-6.239,5.084c0,3.081,2.233,4.467,7.086,6.316   c6.471,2.465,9.782,5.7,9.782,11.246c0,6.547-5.083,11.169-13.941,11.169c-4.082,0-7.856-1.001-10.476-2.542L972.611,55.44z'></path><polygon fill='none' stroke='#010101' points='676.149,23 616.756,23 616.756,1 558.443,34.667 616.756,68.333 616.756,46.333 676.149,46.333 '></polygon></svg>", "parts custom"))
    val bin = TizadaPart(UUID.randomUUID(), "<svg width='2000' height='2000' xmlns='http://www.w3.org/2000/svg' viewBox='0 0 2048 2048'><rect width='511.822' height='339.235' fill='none' stroke='#010101' class='active'></rect></svg>", "mesa de corte")
    val tizadas = mutableListOf(Tizada(UUID.randomUUID(), TizadaConfiguration(), parts, bin, TizadaState.IN_PROGRESS, LocalDateTime.now(), LocalDateTime.now(), "Tizada ejemplo", 200, 100, true))

    override fun getTizada(id: UUID): Tizada? {
        return tizadas.find { it.uuid == id }
    }

    override fun createTizada(t: Tizada) {
        tizadas.add(t)
    }

    override fun getAllTizadas(): Collection<Tizada> {
        return tizadas
    }

    override fun queueTizada(tizada: Tizada): Tizada {
        TODO("Not yet implemented")
    }
}
