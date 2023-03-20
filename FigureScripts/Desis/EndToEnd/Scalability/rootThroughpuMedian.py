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
                         , y=[7441958, 7464154, 7481207, 7489210, 7488381.22, 7439741, 7431010]
                         , line=dict(color=config.desis, width=5)
                         , marker=dict(size=20, symbol='cross')))
fig.add_trace(go.Scatter(name="Intermediate", x=[1, 2, 4, 10, 50, 100, 1000], mode='lines+markers'
                         , y=[16301088.86, 16293672.25, 16297227.1, 16299395.57, 16300529.17, 16297846.27, 16294070.81]
                         , line=dict(color=config.central, width=5)
                         , marker=dict(size=20, symbol='circle')))
# fig.update_traces(marker_color='rgb(158,202,225)', marker_line_color='rgb(8,48,107)', marker_line_width=1.5, opacity=0.6,
# marker=dict(size=10, symbol='triangle-up'))))
#<b>

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
        title_text="events/sec",
        titlefont=dict(size=35),
        exponentformat="e",
        ticktext=["0", "4M", "8M", "12M", "16M"],
        tickvals=[0, 4000000, 8000000, 12000000, 16000000],
        tickmode="array",
        range=[0, 17000000],
        tickfont=dict(size=35),
        # ticks="inside",
        # ticklen=20,
        # tickwidth =5,
    ),
    xaxis=dict(
        title_text="local nodes",
        titlefont=dict(size=35),
        ticktext=["2","10", '10<sup>2<sup>', "10<sup>3<sup>"],
        tickvals=[2, 10, 100, 1000],
        range=[0.3,3.1],
        type="log",
        ticks="inside",
        ticklen=20,
        tickwidth=5,
        tickfont=dict(size=35),
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
fig.update_xaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 )
fig.update_yaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 )
fig.update_yaxes(showgrid=True, gridwidth=1, gridcolor='rgb(120,120,120)')
fig.show()
if not os.path.exists("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1"):
    os.mkdir("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1")
# fig.write_image("images/fig1.svg")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1\/scalability\ThroughputMedianRoot.pdf")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1\/scalability\ThroughputMedianRoot.svg")