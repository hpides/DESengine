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
                         , y=[3268487.831, 3270697.149, 3291610.9, 3295282.32, 3308962.22, 3311921.619, 3298082.798, 3246734.899]
                         , line=dict(color=config.central, width=5), marker=dict(size=20, symbol='circle')))
fig.add_trace(go.Scatter(name="Disco", x=[1, 2, 3, 4, 5, 6, 7, 8], mode='lines+markers'
                         , y=[2181646, 4248293, 6214096.62, 8234068.64, 10462675.8, 13089876, 14869027.39, 16262696.7]
                         , line=dict(color=config.disco, width=5), marker=dict(size=20, symbol='square')))
fig.add_trace(go.Scatter(name="Scotty", x=[1, 2, 3, 4, 5, 6, 7, 8], mode='lines+markers'
                         , y=[4957856.747436678, 5007936.560612149, 4949087.601114224, 5005844.7067284705, 4926535.396857901, 4945576.068979095, 4956379.752601943, 5002667.726287512]
                         , line=dict(color=config.scotty, width=5), marker=dict(size=20, symbol='triangle-up')))
fig.add_trace(go.Scatter(name="Desis", x=[1, 2, 3, 4, 5, 6, 7, 8], mode='lines+markers'
                         , y=[28900099.33, 55234742.1, 83302185.76, 113432812.8, 137752435, 164030639.9, 195964393, 211504416.6
]
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
            size=23,
            color="black"
        ),
    ),
    yaxis=dict(
        title_text="events/sec",
        titlefont=dict(size=35),
        exponentformat="e",
        ticktext=["0", "40M", "80M", "120M", "160M", "200M", "240M"],
        tickvals=[0, 40000000, 80000000, 120000000, 160000000, 200000000, 240000000],
        tickmode="array",
        range=[0, 245000000],
        tickfont=dict(size=35),
        # ticks="inside",
        # ticklen=20,
        # tickwidth =5,
    ),

    xaxis=dict(
        title_text="local nodes",
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
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1\/scalability\ThroughputAverageM.pdf")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1\/scalability\ThroughputAverageM.svg")