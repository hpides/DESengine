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
fig.add_trace(go.Scatter(name="CeBuffer", x=[2, 10, 50, 100, 500, 1000], mode='lines+markers'
                         , y=[90.8, 179.4, 887.8, 1784, 9313.8, 17933]
                         , line=dict(color=config.central, width=5), marker=dict(size=30, symbol='circle')))

fig.add_trace(go.Scatter(name="DeBucket", x=[2, 10, 50, 100, 500, 1000], mode='lines+markers'
                         , y=[90.8, 177.8, 883.4, 1766.4, 8870.4, 17755.6]
                         , line=dict(color=config.desisIC, width=5), marker=dict(size=20, symbol='square')))


fig.add_trace(go.Scatter(name="DeSW", x=[2, 10, 50, 100, 500, 1000], mode='lines+markers'
                         , y=[60.8, 60.8, 61, 61, 60.8, 60.8]
                         , line=dict(color=config.desisSW, width=5), marker=dict(size=30, symbol='triangle-up')))
fig.add_trace(go.Scatter(name="Desis", x=[2, 10, 50, 100, 500, 1000], mode='lines+markers'
                         , y=[60.8, 61, 60.8, 60.8, 60.8, 60.8]
                         , line=dict(color=config.desis, width=5), marker=dict(size=20, symbol='cross')))

# fig.update_traces(marker_color='rgb(158,202,225)', marker_line_color='rgb(8,48,107)', marker_line_width=1.5, opacity=0.6,
# marker=dict(size=10, symbol='triangle-up'))))




# fig.add_trace(go.Scatter(xaxis='x2'))
#legend
fig.update_layout(
    legend=dict(
        orientation="h",
        yanchor="top",
        y=1.2,
        xanchor="left",
        x=-0.05,
        # bordercolor="Black",
        # borderwidth=2,
        # bgcolor="white",
        font=dict(
            size=23,
            color="black"
        ),
    ),
    yaxis=dict(
        title_text="slices in min",
        titlefont=dict(size=35),
        exponentformat="e",
        # ticktext=["0", "3k", "6k", "9k", "12k", "15k", "18k"],
        # tickvals=[0, 100, 500, 1000, 2000, 15000, 18000],
        # tickmode="array",
        # range=[0, 18500],
        tickfont=dict(size=35),
        ticktext=['10<sup>2<sup>', "10<sup>3<sup>", "10<sup>4<sup>", "10<sup>5<sup>"],
        tickvals=[100, 1000, 10000, 100000],
        range=[1.5,5.1],
        type="log",
    ),
    xaxis=dict(
        title_text="concurrent windows",
        titlefont=dict(size=35),
        ticktext=["2","10", '10<sup>2<sup>', "10<sup>3<sup>"],
        tickvals=[2, 10, 100, 1000],
        range=[0.3,3.1],
        type="log",
        ticks = "inside",
        ticklen = 20,
        tickwidth = 5,
        tickfont=dict(size=35),
    ),
    # xaxis2=dict(
    #     ticktext=["50", "500"],
    #     tickvals=[50, 500],
    #     range=[0,3],
    #     type="log",
    #     ticks="inside",
    #     ticklen=20,
    #     tickwidth=5,
    #     overlaying="x",
    #     side="bottom",
    #     tickfont=dict(size=15),
    # )
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
fig.update_xaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 )
fig.update_yaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 )
fig.update_yaxes(showgrid=True, gridwidth=1, gridcolor='rgb(120,120,120)')
fig.show()
if not os.path.exists("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s2"):
    os.mkdir("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s2")
# fig.write_image("images/fig1.svg")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s2\shareWindow\/tumtumSlice.pdf")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s2\shareWindow\/tumtumSlice.svg")