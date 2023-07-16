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


fig.add_trace(go.Scatter(name="Central", x=[1, 2, 3, 4, 5, 6, 7, 8], mode='lines+markers'
                         , y=[255.268487831, 256.3270697149, 257.32916109, 258.329528232, 256.330896222, 255.3311921619, 256.3298082798, 257.3246734899]
                         , line=dict(dash='dash', color=config.central, width=5), marker=dict(size=20, symbol='circle')))
fig.add_trace(go.Scatter(name="Disco", x=[1, 2, 3, 4, 5, 6, 7, 8], mode='lines+markers'
                         , y=[1.1516187, 1.1516187, 1.1516187, 1.1516187, 1.1516187, 1.1516187, 1.1516187, 1.1516187]
                         , line=dict(dash='dashdot', color=config.disco, width=5), marker=dict(size=20, symbol='square')))
fig.add_trace(go.Scatter(name="Scotty", x=[1, 2, 3, 4, 5, 6, 7, 8], mode='lines+markers'
                         , y=[0.566244129, 0.566244129, 0.566244129, 0.566244129, 0.566244129, 0.566244129, 0.566244129, 0.566244129]
                         , line=dict(dash='dot', color=config.scotty, width=5), marker=dict(size=20, symbol='triangle-up')))
fig.add_trace(go.Scatter(name="Deco<sub>async</sub>", x=[1, 2, 3, 4, 5, 6, 7, 8], mode='lines+markers'
                         , y=[0.48621152, 0.34557956, 0.284629888, 0.265841732, 0.3116684, 0.400503279, 0.499353366, 0.6225940037]
                         , line=dict(color=config.decoasy, width=5), marker=dict(size=20, symbol='cross')))

# fig.update_traces(marker_color='rgb(158,202,225)', marker_line_color='rgb(8,48,107)', marker_line_width=1.5, opacity=0.6,
# marker=dict(size=10, symbol='triangle-up'))))

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
            size=30,
            color="black"
        ),
    ),
    yaxis=dict(
        title_text="Latency in ms",
        titlefont=dict(size=35),
        ticktext=["0", "1", "10", "10<sup>2<sup>", "10<sup>3<sup>"],
        tickvals=[0.1, 1, 10, 100, 1000],
        range=[-1,3.1],
        type="log",
        ticks = "inside",
        ticklen = 20,
        tickwidth = 5,
        tickfont=dict(size=35),
    ),

    xaxis=dict(
        title_text="Local Nodes",
        titlefont=dict(size=35),
        ticktext=["1", "2", "3", "4", "5", "6", "7", "8"],
        tickvals=[1, 2, 3, 4, 5, 6, 7, 8],
        range=[1,8.2],
        tickmode="array",
        ticks="inside",
        ticklen=20,
        tickwidth=5,
        tickfont=dict(size=35),
    )
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
# if not os.path.exists("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1"):
#     os.mkdir("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1")
# fig.write_image("images/fig1.svg")
pio.write_image(fig, "E:\On going Paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\s1\/scalability\/LatencyAverageM.pdf")
pio.write_image(fig, "E:\On going Paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\s1\/scalability\/LatencyAverageM.svg")
