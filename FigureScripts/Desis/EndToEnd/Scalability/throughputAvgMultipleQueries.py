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

fig.add_trace(go.Scatter(name="Root", x=[1, 2, 4, 10, 50, 100, 1000], mode='lines+markers'
                         , y=[6015109.39, 5976281.6, 5925974.71, 5951692.9, 5919914.91, 5979043.87, 5898184.71]
                         , line=dict(color=config.desis, width=5)
                         , marker=dict(size=30, symbol='cross')))
fig.add_trace(go.Scatter(name="Intermediate", x=[1, 2, 4, 10, 50, 100, 1000], mode='lines+markers'
                         , y=[6213714.23, 6209130.7, 6232678.33, 6125299.9, 6133912.95, 6147710.9, 6083288.47]
                         , line=dict(color=config.disco, width=5)
                         , marker=dict(size=20, symbol='square')))
fig.add_trace(go.Scatter(name="local", x=[1, 2, 4, 10, 50, 100, 1000], mode='lines+markers'
                         , y=[32801883.73, 31774489.07, 31270061.07, 32186826.55, 31516253.98, 31345975.1, 31029385.73]
                         , line=dict(color=config.central, width=5)
                         , marker=dict(size=20, symbol='circle')))



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
            size=35,
            color="black"
        ),
    ),
    yaxis=dict(
        title_text="Events(Results)/sec",
        titlefont=dict(size=35),
        exponentformat="e",
        ticktext=["0", "5M", "10M", "15M", "20M", "25M", "30M", "35M"],
        tickvals=[0, 5000000, 10000000, 15000000, 20000000, 25000000, 30000000, 35000000],
        tickmode="array",
        range=[0, 36000000],
        tickfont=dict(size=35),
        # ticks="inside",
        # ticklen=20,
        # tickwidth =5,
    ),
    xaxis=dict(
        title_text="Concurrent Windows",
        titlefont=dict(size=35),
        ticktext=["1","10", '10<sup>2<sup>', "10<sup>3<sup>"],
        tickvals=[1, 10, 100, 1000],
        range=[0,3.1],
        type="log",
        ticks = "inside",
        ticklen = 20,
        tickwidth = 5,
        tickfont=dict(size=35),
    ),
    # xaxis2=dict(
    #     ticktext=["2", "50", "500"],
    #     tickvals=[2, 50, 500],
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
if not os.path.exists("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1"):
    os.mkdir("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1")
# fig.write_image("images/fig1.svg")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1\/scalability\ThroughputAverageMultipleQueries.pdf")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1\/scalability\ThroughputAverageMultipleQueries.svg")