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

fig.add_trace(go.Scatter(name="CeBuffer", x=[1, 2, 3, 4, 5, 6, 7, 8], mode='lines+markers'
                         , y=[1563426.905, 1559000.38, 1565001.108, 1558914.97, 1558007.2, 1555151.267, 1543917.05, 1566713.37]
                         , line=dict(color=config.central, width=5), marker=dict(size=20, symbol='circle')))
fig.add_trace(go.Scatter(name="Disco", x=[1, 2, 3, 4, 5, 6, 7, 8], mode='lines+markers'
                         , y=[40379, 81010, 121524, 162212, 202755, 243282, 283521, 324008]
                         , line=dict(color=config.disco, width=5), marker=dict(size=20, symbol='square')))
fig.add_trace(go.Scatter(name="Scotty", x=[1, 2, 3, 4, 5, 6, 7, 8], mode='lines+markers'
                         , y=[7353263, 7359730.37, 7352527, 7356833, 7369556, 7342251, 7352708, 7367361]
                         , line=dict(color=config.scotty, width=5), marker=dict(size=20, symbol='triangle-up')))
fig.add_trace(go.Scatter(name="Desis", x=[1, 2, 3, 4, 5, 6, 7, 8], mode='lines+markers'
                         , y=[7301659.16, 7339741, 7331010, 7441958, 7464154, 7481207, 7489210, 7488381.22]
                         , line=dict(color=config.desis, width=5), marker=dict(size=20, symbol='cross')))

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
            size=35,
            color="black"
        ),
    ),
    yaxis=dict(
        title_text="Events/sec",
        titlefont=dict(size=35),
        exponentformat="e",
        tickmode="array",
        ticktext=["0", "2M", "4M", "6M", "8M"],
        tickvals=[0, 2000000, 4000000, 6000000, 8000000],
        range=[0, 8100000],
        tickfont=dict(size=35),
        # ticks="inside",
        # ticklen=20,
        # tickwidth =5,
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
if not os.path.exists("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1"):
    os.mkdir("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1")
# fig.write_image("images/fig1.svg")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1\/scalability\ThroughputMedianM.pdf")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1\/scalability\ThroughputMedianM.svg")