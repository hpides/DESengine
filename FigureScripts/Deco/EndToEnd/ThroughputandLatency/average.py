import os
import plotly.graph_objects as go
import plotly.io as pio
import plotly.express as px
import os
import sys
# Append parent directory to import path
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
import config
pio.kaleido.scope.mathjax = None


fig = go.Figure()

fig.add_trace(go.Bar(name="Central", x=["Central"], y=[3246734.899], legendrank=1, width=[0.8]
                     , text="3.3M", textposition='outside', textfont = dict(size = 35)
                     , marker_color=config.central, marker_pattern_shape="/"))
fig.add_trace(go.Bar(name="Disco", x=["Disco"], y=[1760157.36], legendrank=2, width=[0.8]
                     , text="1.7M", textposition='outside', textfont = dict(size = 35)
                     , marker_color=config.disco, marker_pattern_shape="."))
fig.add_trace(go.Bar(name="Scotty", x=["Scotty"], y=[8389027.43], legendrank=3, width=[0.8]
                     , text="8.3M", textposition='outside', textfont = dict(size = 35)
                     , marker_color=config.scotty, marker_pattern_shape="x"))
fig.add_trace(go.Bar(name="DecoAsy", x=["Deco<sub>async</sub>"], y=[75850952.88], legendrank=4, width=[0.8]
                     , text="75.9M", textposition='outside', textfont = dict(size = 35)
                     , marker_color=config.decoasy, marker_pattern_shape="+"))
# fig.add_trace(go.Bar(name="DesisSw", x=[" "], y=[30545075.4], legendrank=4, width=[0.18]
#                      , marker_line_color='rgb(255,161,90)', marker_pattern_shape="-"))
# fig.update_traces(marker_color='rgb(158,202,225)', marker_line_color='rgb(8,48,107)', marker_line_width=1.5, opacity=0.6)


#legend
fig.update_layout(showlegend=False)
fig.update_layout(
    # legend=dict(
    #     yanchor="top",
    #     y=0.99,
    #     xanchor="left",
    #     x=0.01,
    #     # bordercolor="Black",
    #     # borderwidth=2,
    #     # bgcolor="white",
    #     font=dict(
    #         size=20,
    #         color="black"
    #     ),
    # ),
    yaxis=dict(
        title_text="events/sec",
        titlefont=dict(size=35),
        exponentformat="e",
        ticktext=["0", "10M", "20M", "30M", "40M", "50M", "60M", "70M", "80M"],
        tickvals=[0, 10000000, 20000000, 30000000, 40000000, 50000000, 60000000, 70000000, 80000000],
        tickmode="array",
        range=[0, 85000000],
        ticks="inside",
        ticklen=20,
        tickwidth =5,
    ),
    xaxis=dict(
        # title_text="local nodes",
        # titlefont=dict(size=15),
        ticktext=["Central", "Disco", "Scotty", "DecoAsy"],
        # tickvals=[1, 2, 3, 4],
        tickmode="array",
        # range=[0, 6],
    ),

)

# size & color
fig.update_layout(
    autosize=False,
    width=660,
    height=440,
    paper_bgcolor='rgba(0,0,0,0)',
    plot_bgcolor='rgba(0,0,0,0)',
    margin=dict(
        l=5,
        r=5,
        b=5,
        t=5,
        pad=0
    ),
)
# fig = px.bar(x=["a","b","c"], y=[1,3,2], color=["red", "goldenrod", "#00D"], color_discrete_map="identity")
fig.update_layout(barmode='group', bargap=0.4)

# fig.update_yaxes(automargin=True)
# fig.update_yaxes(ticks="outside", tickwidth=1, tickcolor='black', ticklen=5)
fig.update_xaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 , tickfont=dict(size=30))
fig.update_yaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 , tickfont=dict(size=35))
# fig.update_yaxes(showgrid=True, gridwidth=1, gridcolor='rgb(120,120,120)')
# ,tickfont_family="Arial Black"

fig.show()
# fig.write_image("images/fig1.svg")
pio.write_image(fig, "E:\On going Paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\s1\/throughputandlatency\ThroughputOverall.pdf")
pio.write_image(fig, "E:\On going Paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\s1\/throughputandlatency\ThroughputOverall.svg")