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

xdata=[2, 10, 50, 100, 500, 1000]

fig = go.Figure()


fig.add_trace(go.Scatter(name="Disco", x=xdata, mode='lines+markers'
                         , y=[3202400000, 3234424000, 3202400000, 3234424000, 3202400000, 3234424000]
                         , line=dict(color=config.disco, width=5), marker=dict(size=25, symbol='square')))
fig.add_trace(go.Scatter(name="CeBuffer", x=xdata, mode='lines+markers'
                         , y=[3170376000, 3202400000, 3170376000, 3202400000, 3170376000, 3202400000]
                         , line=dict(color=config.central, width=5)
                         , marker=dict(size=20, symbol='circle')))


fig.add_trace(go.Scatter(name="Scotty", x=xdata, mode='lines+markers'
                         , y=[13912, 69560, 347800, 695600, 3478000, 6956000]
                         , line=dict(color=config.scotty, width=5), marker=dict(size=20, symbol='triangle-up')))
fig.add_trace(go.Scatter(name="Desis", x=xdata, mode='lines+markers'
                         , y=[464, 2320, 11600, 23200, 116000, 232000]
                         , line=dict(color=config.desis, width=5), marker=dict(size=20, symbol='cross')))

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
        title_text="bytes sent by events",
        titlefont=dict(size=35),
        ticktext=["1B", "1KB", '1MB', "1GB"],
        tickvals=[1, 1000, 1000000, 1000000000],
        range=[0, 10.5],
        type="log",
        ticks="inside",
        ticklen=20,
        tickwidth=5,
        tickfont=dict(size=35),
    ),
    xaxis=dict(
        title_text="distinct keys",
        titlefont=dict(size=35),
        ticktext=["1","10", '10<sup>2<sup>', "10<sup>3<sup>"],
        tickvals=[1, 10, 100, 1000],
        range=[0.3,3.1],
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
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1\/networkoverhead\/networkoverheadMultiplekeys.pdf")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1\/networkoverhead\/networkoverheadMultiplekeys.svg")