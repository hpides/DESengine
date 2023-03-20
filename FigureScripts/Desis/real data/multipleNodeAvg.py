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

fig.add_trace(go.Scatter(name="CeBuffer", x=[1, 2, 3, 4, 5], mode='lines+markers'
                         , y=[46494.372, 46494.372, 46494.372, 46494.372, 46494.372]
                         , line=dict(color=config.central, width=5), marker=dict(size=20, symbol='circle')))
fig.add_trace(go.Scatter(name="Disco", x=[1, 2, 3, 4, 5], mode='lines+markers'
                         , y=[471790.27, 943499.522, 1415421.75, 1886986.76, 2358644.5]
                         , line=dict(color=config.disco, width=5), marker=dict(size=20, symbol='square')))
fig.add_trace(go.Scatter(name="Scotty", x=[1, 2, 3, 4, 5], mode='lines+markers'
                         , y=[3219430.076, 3219869.18, 3219108.58, 3206066.34, 3188692.59]
                         , line=dict(color=config.scotty, width=5), marker=dict(size=20, symbol='triangle-up')))
fig.add_trace(go.Scatter(name="Desis", x=[1, 2, 3, 4, 5], mode='lines+markers'
                         , y=[6439577.13, 12877619.7, 19317240.37, 25724138.54, 32115564.9]
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
        ticktext=["0", "4M", "8M", "16M", "24M", "32M"],
        tickvals=[0, 4000000, 8000000, 16000000, 24000000, 32000000],
        tickmode="array",
        range=[0, 34000000],
        tickfont=dict(size=35),
        # ticks="inside",
        # ticklen=20,
        # tickwidth =5,
    ),

    xaxis=dict(
        title_text="local nodes",
        titlefont=dict(size=35),
        ticktext=["1", "2", "3", "4", "5"],
        tickvals=[1, 2, 3, 4, 5],
        range=[1,5.2],
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
if not os.path.exists("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s3"):
    os.mkdir("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s3")
# fig.write_image("images/fig1.svg")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s3\/Scalability.pdf")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s3\/Scalability.svg")